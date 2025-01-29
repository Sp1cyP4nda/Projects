#!/bin/bash
# The first two while loops create an array using the user input (character name) and prints the message that goes with it.
# The following defines the function to parse out each message and type it out one character at a time.
# The function takes two arguments: The message to be typed out, and the time between each letter being typed
filename=$1
#echo $filename
cd "$(dirname "$0")" # this command assigns the working directory to the one this file is in
cd ../Scripts
date=$(date)
clear
echo
sleep 1
#echo
#echo -n "Enter filename: "
#read filename
# This part is the program finding the filename
./writing.sh "Call trans opt: received. $date REC:Log>" 0.05
sleep 2.5
echo
./writing.sh "Trace program: running" 0.05
sleep 0.1
echo
echo "Name: $filename" > ../Saves/$filename.txt
echo "Found on: $date" >> ../Saves/$filename.txt
echo "Coordinates found: " >> ../Saves/$filename.txt
./location2.sh 0.05 $filename
#sleep 0.5
./writing.sh "$filename found" 0.05
sleep 2.5
clear




# The following begins the game after the filename has been found
Wakeup="Wake up, $filename"
clear
echo
./writing.sh "$Wakeup" 0.05;./blinking.sh 0.05
clear
#./Projects/Bash\ Games/The\ Matrix/Writing.sh "$Wakeup" 0.1

#Wakeup="Wake up $filename"
#i=0
#while [ "$i" -lt ${#Wakeup} ]; do
#  char[${i}]=${Wakeup:${i}:1}
#  ((i ++))
#done

# Now to print the message
#clear
#echo
#t=0
#while [ "$t" -lt $i ]; do
#  echo -n "${char[t]}"
#  sleep 0.1
#  ((t ++))
#done
# This section merely blinks the final period
#clear
#echo
#echo -n "$Wakeup."
#sleep 0.05
#echo -n "."
#sleep 0.05
#echo -n "."
#sleep 1
#clear
#echo
#echo -n "$Wakeup.. "
#sleep 1
#clear
#echo
#echo -n "$Wakeup..."
#sleep 1
#clear
#echo
#echo -n "$Wakeup.. "
#sleep 1
#clear
#echo
#echo -n "$Wakeup..."
#sleep 1
#clear

# This clears the screen then slowly writes The Matrix has you
echo
./writing.sh "The Matrix has you..." 0.25
sleep 5
clear
echo
./writing.sh "Follow the white rabbit." 0.05
sleep 5
clear
echo
echo -n "Knock, knock, $filename."
sleep 5
echo
clear
exit
