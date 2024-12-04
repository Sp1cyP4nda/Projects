#!/bin/bash
###################
###    Mount    ###
###     and     ###
### create ISOs ###
###  ~~~~~~{@   ###
###################
#check if sudo
if [ "$(id -u)" -ne 0 ]; then
    echo "This script must be run as root"
    exit
fi
mkdir -p "./logs/"
mkdir -p './outputs/'
# Input the file here in the following format:
# drive name "single space" workspace name
read -p "Input file: " filename
 
# Take the drive names and declare an two arrays
# > the mount point array
# > the mount drive array
diskNum="$(cat $filename | grep nvme | cut -d ' ' -f1)" # | cut -d 'e' -f2 | cut -d 'n' -f1)" # this is the nvmeXn1p1
disk_array=($diskNum)
for each_disk in "${disk_array[@]}"
do
    mount_point+=("/mnt/$each_disk")
    mount_drive+=("/dev/$each_disk")
done
 
# Do pretty much the same thing, but with the workstations
workstations="$(cat $filename | grep nvme | cut -d ' ' -f2)" # this is the workstation name e.g. ws-83tzvnxxf-data
station_array=($workstations)
for each_station in "${station_array[@]}"
do
    jobs+=("$each_station")
done
for (( i=0; i<${#jobs[@]}; i++ ))
do
    log_file="./logs/${jobs[$i]}.log"
    # First, if root drive, do nothing
    if [[ "${jobs[$i]}" == *"root" ]]
    then
        echo "Skipping ${jobs[i]}" | tee -a "$log_file"
        echo
    else
    # Put the desired code here
        echo >> "$log_file"; echo $(date) | tee -a "$log_file"
        echo "Mounting ${mount_point[$i]}" | tee -a "$log_file"
        mkdir "${mount_point[$i]}"
        mount -t ntfs3 "${mount_drive[$i]}" "${mount_point[$i]}"
 
        echo >> "$log_file" ;echo $(date) | tee -a "$log_file"
        echo "Creating ISO for ${jobs[$i]}" | tee -a "$log_file"
        xorriso -as mkisofs -o "./outputs/${jobs[$i]}.iso" -V "Volume_Label" "${mount_point[$i]}"
 
        echo >> "$log_file" ;echo $(date) | tee -a "$log_file"
        echo "Unmounting ${mount_point[$i]}" | tee -a "$log_file"
        umount "${mount_drive[$i]}" "${mount_point[$i]}"
        rmdir "${mount_point[$i]}"
 
        echo >> "$log_file" ;echo $(date) | tee -a "$log_file"
        echo "${mount_point[$i]} finished" | tee -a "$log_file"
    fi
done