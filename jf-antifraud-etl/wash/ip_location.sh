#!/bin/bash

. ./funclib.sh

export input_dir=/home/neo4j/data/adt
output_dir=$NEO4J_HOME/import/dmp


idx=0
for file in $(find ${input_dir}/order -iname '*.csv'); do
  idx=`expr 1 + $idx`
  num=`printf "%03d" $idx`
  postfix=_${num}.csv

  printf "\nInput File: $file\n"
  echo output Directory: $output_dir/ip_addr
  wash_dmp_order_for_ip_loc $file $output_dir dmporder_ $postfix
done

