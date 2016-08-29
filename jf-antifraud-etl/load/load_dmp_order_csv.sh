#!/bin/bash

. ./inc.sh

for file in $(find ${input_dir}/dmp/order/evt -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  echo $location
  load_order_evt $cmd $location
done

for file in $(find ${input_dir}/dmp/order/tdid_aid -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_reg_tdid_aid $cmd $location
done

for file in $(find ${input_dir}/dmp/order/tdid_idfa -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_reg_tdid_idfa $cmd $location
done

for file in $(find ${input_dir}/dmp/order/tdid_mac -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_reg_tdid_mac $cmd $location
done

