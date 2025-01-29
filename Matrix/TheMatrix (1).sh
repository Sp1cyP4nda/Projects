#!/bin/bash
# This file is the framework that ties the whole game together. It reads
# Whether a save file exists and goes to the checkpoint the player left off
# or starts a new game if a save file doesn't exist

cd "$(dirname "$0")" # Changes directory to be the directory this folder is in

# Game start
clear
echo
echo -n "Enter Filename: "
read filename
#./Saves/$filename.txt
#if [ ./Saves/$filename.txt = './TheMatrix.sh: line 12: ./Saves/$filename.txt: No such file or directory' ]; then
#	./Chapters/Intro.sh
#else
#	echo 'File name found.'
#fi

#cat ./Saves/$filename.txt
# This is the entire file
#ls ./Saves/
name=$(ls ./Saves/ | grep $filename | cut -d '.' -f 1)
#exit
#echo $name
#echo $@
#echo $?
if [ $name == $filename ]; then
	start=$(sed -n 1p ./Saves/$filename.txt)
#	echo $start
#	sleep 1
	./Chapters/$start $filename 0.05
	exit
else
	./Chapters/ChapterOne/Opening $filename
#	echo "Not found"
#	exit
fi
#sleep 1
############################################################################
# The following is exactly what I am looking for, except I need to wrap
# The if statement around it
#cat ./Saves/$filename.txt | while read line; do
#	echo $line
#done
############################################################################
# Okay, so what am I attempting?
#
# Take the user input, and search whether a file (case-INsensitive) exists
# if the file does not exist, then
#	create ./Saves/$filename.txt save file
#	run the ./Intro.sh chapter.
# else
#	scan/read the file to see where the user left off
# 	run from that chapter (Note: this line will expand)
# fi
############################################################################
# The following doesn't work:
#if [ $(find . -type f -iname $filename) = "" ]; then
#	cat ./Saves/$filename.txt | grep "Name: " | cut -d " " -f 2
#else
#	echo "Name: "$filename >> ./Saves/$filename.txt
#fi
############################################################################
# The following function commands will grab the desired string,
# then cut it down to only display the desired part of that string.
# cat ./Saves/<$filename>.txt | grep "<desired info>" | cut -d " " -f 2
############################################################################
# Randomly select starting area. ACTUALLY, what if I give the option
# to choose your starting area and have random be an option. That'd be neat.
# Can also create a bunch of locations, then randomly select a couple or so
# and display them for the player to choose from.
# It seems this filename hasn't yet been contacted.
# Is their General Location known?
# If no, then goes through locating the filename automatically
# (choosing the starting point randomly)
# If yes, then randomly display 3+ starting location to choose from
# something like, where each variable letter is 1 starting location:
# i=$(($RANDOM%<amount of locations>))
# j=$(($RANDOM%<amount of locations>))
# k=$(($RANDOM%<amount of locations>))
# use a while loop instead of an if statement to make sure i,j, and k
# dont match.
# After determining what starting locations to display, send the info
# to the file that holds all the starting locations...for now, don't
# get to hung up on each location having a major impact on the story.
# Although, each location could start differently, that'd be neat.
