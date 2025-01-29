#!/bin/bash
declare -A matrix
num_rows=$2
num_columns=$1
t=$3
function scan
{
k=0
while [ $k -lt $t ]; do
	echo -ne "$(($RANDOM%9))\b"
	sleep 0.5
	((k++))
done
}
#scan
for ((i=1;i<=num_rows;i++)); do
    for ((j=1;j<=num_columns;j++)); do
        matrix[$i,$j]=$(scan)
    done
done

f1="%$((${#num_rows}+1))s"
f2=" %9s"

printf "$f1" ''
for ((i=1;i<=num_rows;i++)) do
    printf "$f2" $i
done
echo

for ((j=1;j<=num_columns;j++)) do
    printf "$f1" $j
    for ((i=1;i<=num_rows;i++)) do
        printf "$f2" ${matrix[$i,$j]}
    done
    echo
done
