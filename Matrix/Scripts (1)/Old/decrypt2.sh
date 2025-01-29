#!/bin/bash
# This is my attempt at shortening ./decrypt.sh
# incidently, this will also give me more control over it
timing=$1
digits=$2
mode=$3
# This first function randomly generates a number with
# as many digits as is inputed by the user
function coordgen
{
count=1
while [ $count -le $digits ];do
	generator=$(($RANDOM%9))
	((count++))
	echo -n $generator
done
}
coords=$(coordgen)
echo -n $coords >> temp.txt # this is just for checking my work
# This function acts to create an array
# using the coords we just created
i=0
while [ "$i" -lt ${#coords} ]; do
	char[${i}]=${coords:${i}:1}
	((i++))
done
# This first iteration scans each digit one at a time,
# then locks a digit before moving onto the next one.
function decrypt
{
tcount=0
dcount=0
while [ $tcount -le $timing ]; do
	if [ $tcount -eq $timing ]; then
		echo -n ${char[$dcount]}
		tcount=0
		((dcount++))
		if [ $dcount -eq $digits ]; then
#			echo
			exit
		fi
	fi
	echo -ne "$(($RANDOM%9))\b"
	((tcount++))
	sleep 0.075
done
}
#decrypt
# In human terms: write an amount of numbers equal
# to $digits (scan), then erase and rewrite them $timing times.
# When $tcount equals $timing write ${char[0]} and scan $digits-1.
# Keep decreasing the scan until $dcount equals $digits,
# aka $dcount-$digits=0
function decryptall
{
tcount=0
dcount=0
while [ $tcount -le $timing ]; do
	if [ $tcount -eq $timing ]; then
		echo -n ${char[$dcount]}
		tcount=0
		((dcount++))
		if [ $dcount -eq $digits ]; then
#			sleep 1
#			echo
			exit
		fi
	fi
	# Now that I have put this together so far, I just need to
	# subtract out numbers as they are locked in
	for i in $(seq 1 $(($digits-$dcount))); do
		# Create a string $digits long
		echo -n "$(($RANDOM%9))"
		sleep 0.0000000000000001
	done
		# then erase the entire string and recreate a new one.
	for i in $(seq 1 $(($digits-$dcount))); do
		echo -ne "\b"
	done
	((tcount++))
done
echo
}
#decryptall
if [ $mode == '' ]; then
	echo "Please provide a mode switch: singular or simultaneous"
	exit
elif [ $mode == "simultaneous" ] || [ $mode == "sim" ] || [ $mode == "2" ]; then
	decryptall
elif [ $mode == "singular" ] || [ $mode == "sin" ] || [ $mode == "1" ]; then
	decrypt
else
	echo "Choose singular or simultaneous"
fi

###############################################
# Keep it simple. only one digit to start
# If more digits are desired, use a for loop
# to add numbers to the coords array
#if [ $digits -eq 1 ]; then
#	coords=$(($RANDOM%9))
#fi
#if [ $digits -eq 2 ]; then
#	coords=$(($RANDOM%9))$(($RANDOM%9))
#fi
#echo $coords

################################################
#declare -A  arr
#arr[0,0]=0 #$(($RANDOM%9))
#arr[0,$t-1]=$(($digits-1))
#arr[0,$t]=$digits #$(($RANDOM%9))
#arr[1,0]=2
#arr[1,1]=3
#echo -ne "${arr[0,0]}${arr[0,$t-1]}${arr[0,$t]}"
#sleep 1
#echo
#echo "${arr[1,0]}${arr[1,1]}"
##################################################
	# Start the same way as the other
	# defining the actual coords
#	coords=$(printf "%10s")
#	echo ${coords// /$(($RANDOM%9))}
#	for i in {1..90}; do
#		echo -ne "$(($RANDOM%9))\b"
#		sleep 0.075
#	done
###################################################
#coords=$(for ((n = 1; n <= digits; n++)); do
#	$(($RANDOM%9))
#	echo -ne $coords
#	sleep 0.075
#done)
#echo $coords
#echo
#for ((i = 1; i <= t; i++)); do
#	echo -ne "$(($RANDOM%9))$(($RANDOM%9))\b\b"
#	sleep 0.075
#done
#echo
######################################################
#declare -A matrix
#num_rows=$t
#num_columns=$digits

#for ((i=1;i<=num_rows;i++)); do
#	for ((j=1;j<=num_cloumns;j++)); do
#		matrix[$i,$j]=$RANDOM
#	done
#done
#
#f1="%$((${#num_rows}+1))s"
#f2="%9s"
#
#printf "$f1" ''
#for ((i=1;i<=num_rows;i++)); do
#	printf "$f2" $i
#done
#echo
#
#for ((j=1;j<=num_columns;j++)); do
#	printf "$f1" $j
#	for ((i=1;i<=num_rows;i++)); do
#		printf "$f2" ${matrix[$i,$j]}
#	done
#	echo
#done
#}
#decrypt
