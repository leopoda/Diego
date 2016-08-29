#!/bin/bash

. ./inc.sh

for file in $(find ${input_dir}/dmp/norm_ip_loc -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_ip_addr_loc $cmd $location
done

