#!/bin/bash
# This is a visual representation
# of the program decrypting the location of $filename
# Next time I work on this, I want to have the code
# stay the same length and actually fully decrypt
# one digit at a time.
decrypt ()
{
	# The first part defines the coords of $filename,
	# Writes them to a variable,
	# Then creates an array using them.
	coords=$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))
#	echo $coords
	i=0
	while [ "$i" -lt ${#coords} ]; do
		char[${i}]=${coords:${i}:1}
		((i ++))
	done
	i=0
	t=100
	while [ $i -lt $t ]; do
		if [ $i -le $(($t*10/100)) ]; then
			echo -ne "$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
			sleep 0.075
		fi
		if [ $i -le $(($t*20/100)) ]&&[ $i -gt $(($t*10/100)) ]; then
			echo -ne "${char[0]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
			sleep 0.075
		fi
		if [ $i -le $(($t*30/100)) ]&&[ $i -gt $(($t*20/100)) ]; then
                        echo -ne "${char[0]}${char[1]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*40/100)) ]&&[ $i -gt $(($t*30/100)) ]; then
                        echo -ne "${char[0]}${char[1]}${char[2]}$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$(($RANDOM%9))$((RANDOM%9))\b\b\b\b\b\b\b\b\b\b"
                        sleep 0.075
                fi
		if [ $i -le $(($t*50/100)) ]&&[ $i -gt $(($t*40/100)) ]; then
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
		if [ $i -eq $t ]; then
                        echo -n $coords
                        sleep 0.075
			echo
			exit
                fi
                ((i ++))
	done
	echo
	sleep 1
}
decrypt
