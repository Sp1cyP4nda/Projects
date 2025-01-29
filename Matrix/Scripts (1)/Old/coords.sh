#!/bin/bash
cd "$(dirname "$0")"
i=$(($RANDOM%2))
j=$(($RANDOM%2))
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
./decrypt2.sh 10 2 1;echo -ne '\xc2\xb0 ';./decrypt2.sh 10 2 1;echo -n "' ";./decrypt2.sh 10 2 1;echo -n ".";./decrypt2.sh 100 4 2;echo -n '" ';NoS
echo
./decrypt2.sh 10 2 1;echo -ne '\xc2\xb0 ';./decrypt2.sh 10 2 1;echo -n "' ";./decrypt2.sh 10 2 1;echo -n ".";./decrypt2.sh 100 4 2;echo -n '" ';EoW

echo
