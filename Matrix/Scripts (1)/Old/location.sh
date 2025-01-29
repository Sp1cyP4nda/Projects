#!/bin/bash
cd "$(dirname "$0")"
i=$(($RANDOM%2))
j=$(($RANDOM%2))
k=$(($RANDOM%2))
timing=$1
function NoS
{
if [ $i -eq 0 ];then
	echo -n "N"
else
	echo -n "S"
fi
}
function EoW
{
if [ $j -eq 0 ];then
	echo -n "W"
else
	echo -n "E"
fi
}
./Writing.sh "Tracing Continent" $timing;./blinking.sh $timing; #echo
./Writing.sh "Continent locked" $timing; sleep 0.1;echo
./Writing.sh "Tracing Country" $timing;./blinking.sh $timing; #echo
./Writing.sh "Country locked" $timing; sleep 0.1;echo
./Writing.sh "Tracing Region" $timing;./blinking.sh $timing; #echo
./Writing.sh "Region locked" $timing;sleep 0.1;echo
./Writing.sh "Tracing City" $timing;./blinking.sh $timing; #echo
./Writing.sh "City locked" $timing;sleep 0.1;echo
./Writing.sh "Scanning coordinates" $timing;echo
./decrypt2.sh 10 2 1;echo -ne '\xc2\xb0 ';./decrypt2.sh 10 2 1;echo -n "' ";./decrypt2.sh 10 2 1;echo -n ".";./decrypt2.sh 100 4 2;echo -n '" ';NoS
echo
./decrypt2.sh 10 2 1;echo -ne '\xc2\xb0 ';./decrypt2.sh 10 2 1;echo -n "' ";./decrypt2.sh 10 2 1;echo -n ".";./decrypt2.sh 100 4 2;echo -n '" ';EoW
echo
./Writing.sh "Coordinates locked" $timing;sleep 0.1;echo
if [ $k -eq 0 ];then
	./Writing.sh "Target is mobile" $timing
	echo
else
	./Writing.sh "Target is stationary" $timing
	echo
fi
