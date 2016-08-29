#!/bin`/bash

export cmd=$NEO4J_HOME/bin/neo4j-shell
export input_dir=/home/neo4j/neo4j-enterprise-3.0.1/import

#export ip_taobao_url=http://ip.taobao.com/service/getIpInfo.php?ip=
#export ip_addr=202.114.129.28
#export cmd_curl="curl -s --connect-timeout 10"


#content=`${cmd_curl} ${ip_taobao_url}${ip_addr}`


function load_reg_evt () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.event as ev, line.time as t, line.ts as ts, line.tdid as tid, line.account as uid
MERGE (x:ADTReg {eventid:ev, timestamp: toInt(ts), time: t})
MERGE (y:TDID {tdid:tid})
MERGE (u:User {user:uid})
MERGE (u)-[:REG]->(x)
MERGE (x)-[:ON_DEVICE]->(y);
  " | $cmd_shell
}

function load_reg_tdid_mac () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.tdid as tid, line.mac as m
MERGE (t:TDID {tdid:tid})
MERGE (y:MAC {mac:m})
MERGE (t)-[:COMPOSE_OF]->(y);
  " | $cmd_shell
}

function load_reg_tdid_aid () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.tdid as tid, line.androidid as aid
MERGE (t:TDID {tdid:tid})
MERGE (a:AndroidId {androidid:aid})
MERGE (t)-[:COMPOSE_OF]->(a);
  " | $cmd_shell
}

function load_reg_tdid_idfa () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.tdid as tid, line.idfa as ia
MERGE (t:TDID {tdid:tid})
MERGE (i:IDFA {idfa:ia})
MERGE (t)-[:COMPOSE_OF]->(i);
  " | $cmd_shell
}

function load_login_evt () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.event as ev, line.time as t, line.ts as ts, line.tdid as tid, line.account as uid
MERGE (x:ADTLogin {eventid:ev, timestamp: toInt(ts), time:t})
MERGE (y:TDID {tdid:tid})
MERGE (u:User {user:uid})
MERGE (u)-[:LOGIN]->(x)
MERGE (x)-[:ON_DEVICE]->(y);
  " | $cmd_shell
}


function load_order_evt () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.event as ev, line.time as t, line.ts as ts, line.amt as amt, line.tdid as tid, line.oid as oid, line.ip as ip
MERGE (x:ADTOrder {eventid:ev, timestamp: toInt(ts), time:t, amount:amt})
MERGE (y:TDID {tdid:tid})
MERGE (o:Order {orderid:oid})
MERGE (p: IPAddr {ipaddr:ip})
MERGE (x)-[:ORDER]->(o)
MERGE (x)-[:IP]->(p)
MERGE (x)-[:ON_DEVICE]->(y);
  " | $cmd_shell
}


function load_pay_evt () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.event as ev, line.time as t, line.ts as ts, line.amt as amt, line.tdid as tid, line.oid as oid, line.ip as ip
MERGE (x:ADTPay {eventid:ev, timestamp: toInt(ts), time:t, payamount:amt})
MERGE (y:TDID {tdid:tid})
MERGE (o:Order {orderid:oid})
MERGE (p: IPAddr {ipaddr:ip})
MERGE (x)-[:PAY]->(o)
MERGE (x)-[:IP]->(p)
MERGE (x)-[:ON_DEVICE]->(y);
  " | $cmd_shell
}


function load_ip_addr_loc () {
  cmd_shell=$1
  csv_file=$2

  echo "
USING PERIODIC COMMIT 1000
LOAD CSV WITH HEADERS FROM 'file:///$csv_file' AS line
WITH line.ip as ip, line.country_id as cid, line.country as c, line.area_id as aid, line.area as a, line.region_id as rid, line.region as r, line.city_id as ctid, line.city as ct, line.county_id as cntyid, line.county as cnty, line.isp_id as ispid, line.isp as sp
MERGE (o: IPAddr {ipaddr:ip})
MERGE (p: Country {country_id:cid, country:c})
MERGE (q: Area {area_id:aid, area:a})
MERGE (s: Region {region_id:rid, region:r})
MERGE (t: City {city_id:ctid, city:ct})
MERGE (u: County {county_id:cntyid, county:cnty})
MERGE (v: Isp {isp_id:ispid, isp:sp})
MERGE (o)-[:ISP]->(v)
MERGE (v)-[:COUNTY]->(u)
MERGE (u)-[:CITY]->(t)
MERGE (t)-[:REGION]->(s)
MERGE (s)-[:AREA]->(q)
MERGE (q)-[:COUNTRY]->(p);
  " | $cmd_shell
}

