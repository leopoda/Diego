#!/bin/bash

#export csv_data_file=dmpreg_000.00.csv
#export outdir=$NEO4J_HOME/import/dmp/reg
#export out_csv_prefix=dmpreg_
#export out_csv_postfix=_000_00.csv

export ip_taobao_url=http://ip.taobao.com/service/getIpInfo.php?ip=
export cmd_curl="curl -s --connect-timeout 10"

# ---------------------------------------------------
# 清洗 adt 注册事件的数据
# 注: 登录事件和注册事件结构相同, 可以共用这个清洗模块
# ---------------------------------------------------
function wash_dmp_reg_csv () {
  csv_data_file=$1
  outdir=$2
  out_csv_prefix=$3
  out_csv_postfix=$4

  mkdir -p ${outdir}

  # ------------------------------------------------------
  # output csv file which contains event, tdid and account
  # ------------------------------------------------------
  echo event, tdid and account...
  out_csv_file1=${outdir}/evt/${out_csv_prefix}evt${out_csv_postfix}

  mkdir -p ${outdir}/evt
  ../script/reg_extract_event_tdid_account.awk ${csv_data_file} > ${out_csv_file1}

  # ------------------------------------------------------
  # output csv file which contains tdid and mac
  # ------------------------------------------------------
  echo tdid and mac...
  out_csv_file2=${outdir}/tdid_mac/${out_csv_prefix}tdid_mac${out_csv_postfix}

  mkdir -p ${outdir}/tdid_mac
  ../script/reg_extract_tdid_mac.awk ${csv_data_file} > ${out_csv_file2}

  # ------------------------------------------------------
  # output csv file which contains tdid and androidid
  # ------------------------------------------------------
  echo tdid and androidid...
  out_csv_file3=${outdir}/tdid_aid/${out_csv_prefix}tdid_aid${out_csv_postfix}

  mkdir -p ${outdir}/tdid_aid
  ../script/reg_extract_tdid_aid.awk ${csv_data_file} > ${out_csv_file3}

  # ------------------------------------------------------
  # output csv file which contains tdid and idfa
  # ------------------------------------------------------
  echo tdid and idfa...
  out_csv_file4=${outdir}/tdid_idfa/${out_csv_prefix}tdid_idfa${out_csv_postfix}

  mkdir -p ${outdir}/tdid_idfa
  ../script/reg_extract_tdid_idfa.awk ${csv_data_file} > ${out_csv_file4}
}

# ----------------------------------
# 清洗 adt 订单事件的数据
# 注: 订单事件和支付事件结构相同, 可以共用这个清洗模块
# ----------------------------------
function wash_dmp_order_csv () {
  csv_data_file=$1
  outdir=$2
  out_csv_prefix=$3
  out_csv_postfix=$4

  mkdir -p ${outdir}

  # ------------------------------------------------------
  # output csv file which contains event, tdid, order and ip
  # ------------------------------------------------------
  echo event, tdid, orderid and ip address...
  out_csv_file1=${outdir}/evt/${out_csv_prefix}evt${out_csv_postfix}

  mkdir -p ${outdir}/evt
  ../script/ord_extract_event_tdid_orderid.awk ${csv_data_file} > ${out_csv_file1}

  # ------------------------------------------------------
  # output csv file which contains tdid and mac
  # ------------------------------------------------------
  echo tdid and mac...
  out_csv_file2=${outdir}/tdid_mac/${out_csv_prefix}tdid_mac${out_csv_postfix}

  mkdir -p ${outdir}/tdid_mac
  ../script/ord_extract_tdid_mac.awk ${csv_data_file} > ${out_csv_file2}

  # ------------------------------------------------------
  # output csv file which contains tdid and androidid
  # ------------------------------------------------------
  echo tdid and androidid...
  out_csv_file3=${outdir}/tdid_aid/${out_csv_prefix}tdid_aid${out_csv_postfix}

  mkdir -p ${outdir}/tdid_aid
  ../script/ord_extract_tdid_aid.awk ${csv_data_file} > ${out_csv_file3}

  # ------------------------------------------------------
  # output csv file which contains tdid and idfa
  # ------------------------------------------------------
  echo tdid and idfa...
  out_csv_file4=${outdir}/tdid_idfa/${out_csv_prefix}tdid_idfa${out_csv_postfix}

  mkdir -p ${outdir}/tdid_idfa
  ../script/ord_extract_tdid_idfa.awk ${csv_data_file} > ${out_csv_file4}
}


function wash_dmp_order_for_ip_loc () {
  csv_data_file=$1
  outdir=$2
  out_csv_prefix=$3
  out_csv_postfix=$4

  mkdir -p ${outdir}/ip_addr
  out_csv=${outdir}/ip_addr/${out_csv_prefix}ip_addr${out_csv_postfix}

  cat ${csv_data_file} | awk 'BEGIN {FS = "\t";} NR >= 2 {ip = $5; if (ip != "") print ip;}' | sort | uniq | awk -v x="${cmd_curl} ${ip_taobao_url}" '{cmd=sprintf("%s%s\n", x , $1); system(cmd) | getline line; print line;}' > ip.tmp

  for line in `cat ip.tmp`
  do
    #echo $line
    content=`echo $line | jq 'select(.code == 0) | .data | .ip,.country_id,.country,.area_id,.area,.region_id,.region,.city_id,.city,.county_id,.county,.isp_id,.isp' | tr '\n' ','`
    echo $content | sed -e '/"/s/"//g' >>${out_csv}
  done

  rm -rf ./ip.tmp
}


function normalizeIPAddrLocation() {
 input_file=$1
 output_file=$2
 ../script/ipaddr.awk ${input_file} > ${output_file}
}
