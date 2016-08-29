#!/bin/bash

export input_dir=/home/neo4j/data/adt
output_dir=$NEO4J_HOME/import/dmp

. ./funclib.sh

idx=0
for file in $(find ${output_dir}/ip_addr -iname '*.csv'); do
  idx=`expr 1 + $idx`
  num=`printf "%03d" $idx`
  postfix=_${num}.csv

  printf "\nInput File: $file\n"
  echo output Directory: $output_dir/norm_ip_loc
  mkdir -p $output_dir/norm_ip_loc
  normalizeIPAddrLocation $file $output_dir/norm_ip_loc/norm_ip_loc${postfix}
done

