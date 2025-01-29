#!/bin/bash
# This makes the elipses blink as though searching
timing=$1
echo -n "."
sleep $timing
echo -n "."
sleep $timing
echo -n "."
sleep 1
echo -ne "\b "
sleep 1
echo -ne "\b."
sleep 1
echo -ne "\b "
sleep 1
echo -ne "\b."
sleep 1
#echo -ne "\b "
#sleep 1
#echo -ne "\b."
#sleep 1
#echo -ne "\b "
#sleep 1
echo
