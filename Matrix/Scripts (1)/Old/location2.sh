#!/bin/bash
cd "$(dirname "$0")"
i=$(($RANDOM%2))
j=$(($RANDOM%2))
k=$(($RANDOM%2))
timing=$1
filename=$2
function NoS
{
if [ $i -eq 0 ];then
	echo -n "N"
	echo "N" >> temp.txt
else
	echo -n "S"
	echo "S" >> temp.txt
fi
}
function EoW
{
if [ $j -eq 0 ];then
	echo -n "W"
	echo "W" >> temp.txt
else
	echo -n "E"
	echo "E" >> temp.txt
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

#########################################################################
#
# look in file
# First line; insert *; no line break
# read second line; insert '; no line break
# read third line; insert "; no line break
#
#########################################################################

#cat temp.txt
sed -r -e 's/^.{2}/&\xc2\xb0 /' temp.txt > temp2.txt
#cat temp2.txt
sed -r -e "s/^.{6}/&' /" temp2.txt > temp.txt
#cat temp.txt
sed -r -e 's/^.{10}/&./' temp.txt > temp2.txt
#cat temp2.txt
sed -r -e 's/^.{15}/&" /' temp2.txt > temp.txt
#cat temp.txt
cat temp.txt >> ../Saves/$filename.txt
rm temp.txt temp2.txt
exit
