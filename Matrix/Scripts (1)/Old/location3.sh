#!/bin/bash
cd "$(dirname "$0")"
i=$(($RANDOM%2))
j=$(($RANDOM%2))
k=$(($RANDOM%2))
timing=$1
filename=$2
coords=$3
i=0
while [ "$i" -lt ${#coords} ];do
	char[${i}]=${coords:${i}:1}
	((i++))
done
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
./Writing.sh "Continent found - $6" $timing; sleep 0.1;echo
./Writing.sh "Tracing Country" $timing;./blinking.sh $timing; #echo
./Writing.sh "Country found - $7" $timing; sleep 0.1;echo
./Writing.sh "Tracing Region" $timing;./blinking.sh $timing; #echo
./Writing.sh "Region found - $8" $timing;sleep 0.1;echo
./Writing.sh "Tracing City" $timing;./blinking.sh $timing; #echo
./Writing.sh "City found - $9" $timing;sleep 0.1;echo
./Writing.sh "Scanning coordinates" $timing;echo
#echo ${char[0]}${char[1]}
./decrypt3.sh 10 2 1 ${char[0]}${char[1]}; echo -ne '\xc2\xb0 ';./decrypt3.sh 10 2 1 ${char[2]}${char[3]};echo -n "' ";./decrypt3.sh 10 2 1 ${char[4]}${char[5]};echo -n ".";./decrypt3.sh 100 4 2 ${char[6]}${char[7]}${char[8]}${char[9]};echo -n '" ';echo -n $4
echo
echo -n $4 >> temp.txt # This puts the N/S coordinate into the file
echo ' ' >> temp.txt # This spaces stuff out
./decrypt3.sh 10 2 1 ${char[10]}${char[11]};echo -ne '\xc2\xb0 ';./decrypt3.sh 10 2 1 ${char[12]}${char[13]};echo -n "' ";./decrypt3.sh 10 2 1 ${char[14]}${char[15]};echo -n ".";./decrypt3.sh 100 4 2 ${char[16]}${char[17]}${char[18]}${char[19]};echo -n '" ';echo -n $5
echo
echo $5 >> temp.txt # This puts the E/W coordinate into the file
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

sed -r -e 's/^.{2}/&\xc2\xb0 /' temp.txt > temp2.txt
sed -r -e "s/^.{6}/&' /" temp2.txt > temp.txt
sed -r -e 's/^.{10}/&./' temp.txt > temp2.txt
sed -r -e 's/^.{15}/&" /' temp2.txt > temp.txt
#echo $(10)>>temp.txt
cat temp.txt >> ../Saves/$filename.txt
echo "" >> ../Saves/$filename.txt
echo "Coordinates Decoded To:" >> ../Saves/$filename.txt
echo ${10} >> ../Saves/$filename.txt
echo "$9, $8" >> ../Saves/$filename.txt
echo $7 >> ../Saves/$filename.txt
echo "" >> ../Saves/$filename.txt
rm temp.txt temp2.txt
exit
