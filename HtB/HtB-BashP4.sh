#!/bin/bash
#2paTlJYTkxDZz09Cg== <- right answer
var="8dm7KsjU28B7v621Jls"
value="ERmFRMVZ0U2paTlJYTkxDZz09Cg"

for i in {1..40}
do
        var=$(echo $var | base64)
#        echo $var | grep $value
        check=$(echo $var | grep $value)
#        echo $check
		count=$(echo $var | wc -c)
		if [ $count -gt 113450 ] #&& [[ $check == $value ]] <- find out why this does work
		then
			var2=$(echo $var | tail -c 20)
			echo "Condition met at iteration $i"
			echo "Last 20 characters: $var2"
			echo "Number of characters: $($var | wc -c)"
			break
		else
			echo "Iteration $i: Condition not met."
		fi
		if [ $i -eq 40 ]
		then
			echo "Condition not met. Rerun? (y/N)"
			read yn
			if [[ $yn != "N" ]] && [[ $yn != "n" ]]
			then
				bash ~/HtB/HtB-BashP4.sh
			fi
		fi
done
