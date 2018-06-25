#!/bin/bash

#path_script="/Users/achir/Documents/coding/NS3/nswave/ns-3.28/"
current_path=$(pwd)
path_script="$current_path/../ns-3.28/"
#"/Users/achir/Documents/coding/NS3/nswave/ns-3.28/"
command="./waf --run "\""wave-simple-80211p --senderPosition="

# RSU positions
RSU1=5.0
RSU2=135.0
RSU3=265.0

# internal variables
min=0
max=270
step=15

#
pattern_file="results.pattern"

echo "Simulations"

cd $path_script
for (( node_position=$min; node_position<=$max; node_position=num=$((node_position + step)) ))
do
	echo "==========================="
	echo "node is at $node_position m"
	echo "run simulation..."
	output_file="results_$node_position.output.tr"
	command_to_run="$command$node_position --RSU1Position=$RSU1 --RSU2Position=$RSU2 --RSU3Position=$RSU3"\"""
	echo "command=$command_to_run"
	eval "$command_to_run" | sed -e '1 d ; 2 d ; 3 d ; 4 d' > "$current_path/$output_file"
done

sleep 2

# goto analysis folder 
cd $current_path
echo "Data analysis..."
echo "compiling java..."
eval "javac com/achir/*.java"
echo "analysis..."
eval "java com.achir.Main $max $min $step /Users/achir/Documents/coding/NS3/nswave/runSimulationINRIA results_$RSU1-$RSU2-$RSU3.output.txt"

# # prepare result file
# results="results$RSU1-$RSU2-$RSU3.txt"
# if [ -f $results ] ; then
#     rm $results
# fi
# touch $results

# # start analysis
# for (( node_position=$min; node_position<=$max; node_position=num=$((node_position + step)) ))
# do
# 	echo "==========================="
# 	echo "node@ $node_position"
# 	output_file="results_$node_position.output"
# 	tmp_output_file="$output_file.tmp1"
# 	tmp_output_file_bis="$output_file.tmp2"

# 	##sed '/^tx/ d' $output_file | cut -d"," -f 1,4,16 | tr '=' ',' | cut -d"," -f 1,3,5 | tr ',' ' ' >  $tmp_output_file # with OK/NOK
	
# 	# get the wanted fields
# 	sed '/^tx/ d' $output_file \
# 			| cut -d"," -f 4,16 \
# 			| tr '=' ',' \
# 			| cut -d"," -f 2,4 \
# 			| tr ',' ' ' \
# 			>  $tmp_output_file

#     # sort the results 
# 	cut -f 2 $tmp_output_file \
# 			| sort -k1,1 \
# 			> $tmp_output_file_bis

#     # add the missing lines 
# 	mv $tmp_output_file_bis $tmp_output_file
# 	awk 'NR==FNR{a[$1]=$2; next}{if(!($1 in a)) {print $1,a[$1],$2} else {print $1,a[$1]}}' $tmp_output_file $pattern_file > $tmp_output_file_bis
# 	mv $tmp_output_file_bis $tmp_output_file

#     # put all the results in the same line
# 	cut -f 2 $tmp_output_file \
# 			| tr '\n' ' ' \
# 			> $tmp_output_file_bis

# 	cat <(echo -n "$node_position ") $tmp_output_file_bis <(echo ) >> $results

# done

#rm *.output *.tmp*

#awk 'NR>1 {print $1 "\t" $2 "\t\t\t" $3 "\t\t\t" $4 "\t\t\t" $5 "\t\t\t" $6 "\t\t\t" $7}' $results > "tmp.tmp"
