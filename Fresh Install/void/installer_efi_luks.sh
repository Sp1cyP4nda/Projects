#First things first
# From inside a live installer

##############################
###                        ###
### User-Defined Variables ###
###			   ###
##############################

# Display lsblk
is_valid_name=FALSE
count=0
lsblk
while [ $is_valid_name = FALSE ];do
read -rp "Enter Block Name (Don't include the partition): " device_block_name
if [[ "$device_block_name" != "/dev/"* ]];then device_block_name="/dev/$device_block_name";fi
[[ "$(lsblk -dno TYPE $device_block_name)" == "disk" ]] && is_valid_name=TRUE || is_valid_name=FALSE
((count++))
if (( count >= 10 ));then exit;fi
done
read -rp "Enter Machine Name (No Spaces): " desired_machine_name
read -rp "Enter Desired Swap Size (e.g. 2G is 2GB): " desired_swap_size
read -rp "Is this a new LUKS device? (y/N) " is_new_luks_device
if [[ "${is_new_luks_device,,}" = "y" || "${is_new_luks_device,,}" = "yes" ]];then
read -rp "Repartition the drive? (y/N) " repartition
fi

################################
###			     ###
### System-Defined Variables ###
###			     ###
################################

# If this is a new luks device, 
## disk has partition table:
checkpt=(lsblk -dno pttype $device_block_name)
"${checkpt[@]}" 2&>/dev/null && has_pttable=TRUE || has_pttable=FALSE
## disk has to be gpt:
checkgpt=$("${checkpt[@]}")
[[ "$checkgpt" = "gpt" ]] && is_gpt=TRUE || is_gpt=FALSE
## disk has to be efi
[ -d /sys/firmware/efi ] && is_efi=TRUE || is_efi=FALSE
machine_root="/dev/$(echo $desired_machine_name)/root"
machine_home="/dev/$(echo $desired_machine_name)/home"
machine_swap="/dev/$(echo $desired_machine_name)/swap"
machine_mapper="/dev/mapper/$(echo $desired_machine_name)"
drive_type=$(lsblk -dno TRAN $(echo $device_block_name))
partition_boot=''
partition_main=''
if [[ "$drive_type" == "nvme" ]];then
partition_boot="$(echo $device_block_name)p1"
partition_main="$(echo $device_block_name)p2"
else
partition_boot="$(echo $device_block_name)1"
partition_main="$(echo $device_block_name)2"
fi

##########################
###		       ###
### Running the Script ###
###		       ###
##########################

# Prep the drive, if needed
if [[ "${repartition,,}" == "yes" || "${repartition,,}" == "y" ]];then
cfdisk $(echo $device_block_name)
fi

# Stop the script if not EFI
if [[ "$is_efi" == FALSE ]];then
echo "This script requires EFI"
exit
fi

# If this is a new luks device, format
if [[ "${is_new_luks_device,,}" == "yes" || "${is_new_luks_device,,}" == "y" ]];then
cryptsetup luksFormat --type luks1 $(echo $partition_main)
fi

# Open the luks device
echo
echo "Opening LUKS Device"
cryptsetup luksOpen $(echo $partition_main) $(echo $desired_machine_name)
vgcreate $(echo $desired_machine_name) $(echo $machine_mapper)
lvcreate --yes --name root -L 32G $(echo $desired_machine_name)
lvcreate --yes --name swap -L $(echo $desired_swap_size) $(echo $desired_machine_name)
lvcreate --yes --name home -l 100%FREE $(echo $desired_machine_name)
mkfs.xfs -L root $(echo $machine_root)
mkfs.xfs -L home $(echo $machine_home)
mkswap $(echo $machine_swap)
swapon $(echo $machine_swap)

# Installing the system
## Mount the system
mount $(echo $machine_root) /mnt
mkdir -p /mnt/home
mount $(echo $machine_home) /mnt/home
mkfs.vfat $(echo $partition_boot)
mkdir -p /mnt/boot/efi
mount $(echo $partition_boot) /mnt/boot/efi
## Copy the RSA keys
mkdir -p /mnt/var/db/xbps/keys
cp /var/db/xbps/keys/* /mnt/var/db/xbps/keys/
# Install the system from local
tar --create --one-file-system --xattrs -f - / 2>/dev/null | tar --extract --xattrs --xattrs-include='*' --preserve-permissions -v -f - -C /mnt #2&>/dev/null

# xChroot Configuration
## FS Config
xgenfstab /mnt > /mnt/etc/fstab
## Enter chroot manually
mount -t proc none /mnt/proc
mount -t sysfs none /mnt/sys
mount --rbind /dev /mnt/dev
mount --rbind /run /mnt/run
#exit
#chroot /mnt
# Create the script that will run in the chroot
cat > /mnt/run.sh << 'EOF'
#!/bin/bash
device_block_name=$1
desired_machine_name=$2
partition_boot=$3
partition_main=$4
chown root:root /
chmod 755 /
echo "Set root password"
until (passwd root);do echo "Try again";done
echo $(echo $desired_machine_name) > /etc/hostname
## System Locale (glibc only)
echo "LANG=en_US.UTF-8" > local.conf
echo "en_US.UTF-8 UTF-8" >> libc-locales
ln -sf /usr/share/zoneinfo/US/Eastern /etc/localtime
xbps-reconfigure -f glibc-locales
## GRUB config
cp /etc/default/grub /etc/default/grub.bak
echo "GRUB_ENABLE_CRYPTODISK=y" >> /etc/default/grub
UUID=$(blkid -o value -s UUID $(echo $partition_main))
PATTERN="GRUB_CMDLINE_LINUX_DEFAULT=\"loglevel=4\""
REPLACEMENT="GRUB_CMDLINE_LINUX_DEFAULT=\"loglevel=4 rd.lvm.vg=$(echo $desired_machine_name) rd.luks.uuid=$(echo $UUID)\""
sed -i "s|$(echo $PATTERN)|$(echo $REPLACEMENT)|g" /etc/default/grub
## LUKS key setup 
dd bs=1 count=64 if=/dev/urandom of=/boot/volume.key
echo "Enter decryption password to add the luks key to boot"
cryptsetup luksAddKey $(echo $partition_main) /boot/volume.key # The device block name is the other one (nvme0n1p1 instead of the one nvme0n1p2)
chmod 000 /boot/volume.key
chmod -R g-rwx,o-rwx /boot
### Add keyfile to /etc/fstab (use nvme0n1p2)
echo "$(echo $desired_machine_name) $(echo $partition_main) /boot/volume.key luks" >> /etc/crypttab
### Do the /etc/dracut.conf.d/10-crypt.conf file stuff
mkdir -p /etc/dracut.conf.d/
touch /etc/dracut.conf.d/10-crypt.conf
echo "install_items+=\" /boot/volume.key /etc/crypttab \"" > /etc/dracut.conf.d/10-crypt.conf

#######################################
###				    ###
### Post-Installation Configuration ###
###				    ###
#######################################

# Setup a sudo account
read -rp "Enter name of sudo account (lowercase only): " sudo_acct
useradd $(echo $sudo_acct)
usermod -aG wheel $(echo $sudo_acct)
echo "Enter a secure password. DO NOT FORGET THIS PASSWORD:"
#until passwd;do
until (passwd $(echo $sudo_acct));do echo "Try again";done
## Ask if setup non-sudo acct
read -rp "Enter name of standard user (lowercase only): " std_acct
useradd $(echo $std_acct)
echo "Enter a secure password:"
until (passwd $(echo $std_acct));do echo "Try again";done
userdel -r anon

#########################
###		      ###
### GRUB Installation ###
###                   ###
#########################

#xbps-install -S grub-$(uname -m)-efi
mount -t efivarfs none /sys/firmware/efi/efivars
#grub-install --target=$(uname -m)-efi --efi-directory=/boot/efi --bootloader-id="Void"

###################################
###			        ###
### Post-Installation Hardening ###
###				###
###################################

# Block root access
passwd -l root
usermod -s /usr/sbin/nologin root
# Block interactive for shadow, gshadow, passwd, and group
chattr +i /etc/shadow /etc/gshadow /etc/passwd /etc/group
# Copy in preconFiles
# Lockdown GRUB (make bak first)
# aa-configurations
update-grub

####################################
###				 ###
### Complete System Installation ###
###				 ###
####################################

grub-install $(echo $device_block_name)
xbps-reconfigure -fa
exit
EOF
chmod +x /mnt/run.sh
chroot /mnt /run.sh $device_block_name $desired_machine_name $partition_boot $partition_main
umount -R /mnt
shutdown -r now
