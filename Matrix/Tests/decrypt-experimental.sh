#!/bin/bash
# This is a visual representation
# of the program decrypting the location of $filename
# Next time I work on this, I want to have the code
# stay the same length and actually fully decrypt
# one digit at a time.
t=$1      # This is the timing it takes to decrypt each digit
digits=$2 # This is how many digits you want to decrypt, up to 10
if [ $digits -gt 10 ]; then echo "Can only have up to 10 digits"; exit; fi
decrypt ()
{
	# This part defines the coords of $filename,
	# Writes them to a variable,
	# Then creates an array using them.
	coords=$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))
#	echo $coords
	i=0
	while [ "$i" -lt ${#coords} ]; do
		char[${i}]=${coords:${i}:1}
		((i ++))
	done
	echo $coords
	i=0
	while [ $i -lt $t ]; do
		if [ $i -le $(($t*10/100)) ]&&[ $digits -ge 1 ]; then
			echo -ne "$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
			sleep 0.075
		fi
		if [ $i -le $(($t*20/100)) ]&&[ $i -gt $(($t*10/100)) ]&&[ $digits -ge 2 ]; then
			echo -ne "${char[0]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
			sleep 0.075
		fi
		if [ $i -le $(($t*30/100)) ]&&[ $i -gt $(($t*20/100)) ]&&[ $digits -ge 3 ]; then
                        echo -ne "${char[0]}${char[1]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*40/100)) ]&&[ $i -gt $(($t*30/100)) ]&&[ $digits -ge 4 ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*50/100)) ]&&[ $i -gt $(($t*40/100)) ]&&[ $digits -ge 5 ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}${char[3]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*60/100)) ]&&[ $i -gt $(($t*50/100)) ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}${char[3]}${char[4]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*70/100)) ]&&[ $i -gt $(($t*60/100)) ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}${char[3]}${char[4]}${char[5]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*80/100)) ]&&[ $i -gt $(($t*70/100)) ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}${char[3]}${char[4]}${char[5]}${char[6]}$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*90/100)) ]&&[ $i -gt $(($t*80/100)) ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}${char[3]}${char[4]}${char[5]}${char[6]}${char[7]}$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -lt $(($t)) ]&&[ $i -gt $(($t*90/100)) ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}${char[3]}${char[4]}${char[5]}${char[6]}${char[7]}${char[8]}$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
#		if [[ $i -ge $t ]]; then
#                        echo -n "$coords"
#                        sleep 0.075
#                fi
                ((i ++))
	done
	echo "$coords"
	sleep 1
}
decrypt
