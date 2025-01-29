#!/bin/bash
# This program is to define a function that takes any string
# Then simulates it being typed out like in the 
# Opening scene of the matrix
MESSAGE=$1
TIMING=$2
function writing
{
  i=0
  while [ "$i" -lt ${#MESSAGE} ]; do
    char[${i}]=${MESSAGE:${i}:1}
    ((i ++))
  done
#  clear
  t=0
  while [ "$t" -lt $i ]; do
#   echo
    echo -n "${char[t]}"
    sleep $TIMING
    ((t ++))
  done
}
#writing
function writingdefault
{
  i=0
  while [ "$i" -lt ${#MESSAGE} ]; do
    char[${i}]=${MESSAGE:${i}:1}
    ((i ++))
  done
#  clear
  t=0
  while [ "$t" -lt $i ]; do
#   echo
    echo -n "${char[t]}"
    sleep 0.05
    ((t ++))
  done
}
if $TIMING == ''; then
	writingdefault
elif
	writing
fi
