#!/bin/bash

export input_dir=/home/neo4j/data/adt
output_dir=$NEO4J_HOME/import/dmp

. ./funclib.sh

printf "\n开始处理注册事件数据...\n"
idx=0
for file in $(find ${input_dir}/reg -iname '*.csv'); do
  idx=`expr 1 + $idx`
  num=`printf "%03d" $idx`
  postfix=_${num}.csv

  printf "\nInput File: $file\n"
  echo output Directory: $output_dir/reg
  wash_dmp_reg_csv $file $output_dir/reg dmpreg_ $postfix 
done

printf "\n开始处理登录事件数据...\n"
idx=0
for file in $(find ${input_dir}/login -iname '*.csv'); do
  idx=`expr 1 + $idx`
  num=`printf "%03d" $idx`
  postfix=_${num}.csv

  printf "\nInput File: $file\n"
  echo output Directory: $output_dir/login
  wash_dmp_reg_csv $file $output_dir/login dmplogin_ $postfix 
done


printf "\n开始处理订单事件数据...\n"
idx=0
for file in $(find ${input_dir}/order -iname '*.csv'); do
  idx=`expr 1 + $idx`
  num=`printf "%03d" $idx`
  postfix=_${num}.csv

  printf "\nInput File: $file\n"
  echo output Directory: $output_dir/order
  wash_dmp_order_csv $file $output_dir/order dmporder_ $postfix
done


printf "\n开始处理支付事件数据...\n"
idx=0
for file in $(find ${input_dir}/pay -iname '*.csv'); do
  idx=`expr 1 + $idx`
  num=`printf "%03d" $idx`
  postfix=_${num}.csv

  printf "\nInput File: $file\n"
  echo output Directory: $output_dir/pay
  wash_dmp_order_csv $file $output_dir/pay dmppay_ $postfix
done

