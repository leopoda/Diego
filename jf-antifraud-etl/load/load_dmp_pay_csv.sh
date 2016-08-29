#!/bin/bash

. ./inc.sh

for file in $(find ${input_dir}/dmp/pay/evt -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_pay_evt $cmd $location
done

for file in $(find ${input_dir}/dmp/pay/tdid_aid -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_reg_tdid_aid $cmd $location
done

for file in $(find ${input_dir}/dmp/pay/tdid_idfa -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_reg_tdid_idfa $cmd $location
done

for file in $(find ${input_dir}/dmp/pay/tdid_mac -iname '*.csv'); do
  location=`echo $file ${input_dir} | awk '{idx = length($2); s = substr($1, idx+2); print s;}'`
  load_reg_tdid_mac $cmd $location
done

