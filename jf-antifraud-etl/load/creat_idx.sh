#!/bin/bash

. ./inc.sh
echo create indexs before batch importing to neo4j database...

echo "CREATE INDEX ON :ADTReg (eventid);" | $cmd
echo "CREATE INDEX ON :TDID (tdid);" | $cmd
echo "CREATE INDEX ON :User (user);" | $cmd
echo "CREATE INDEX ON :MAC (mac);" | $cmd
echo "CREATE INDEX ON :AndroidId (androidid);" | $cmd
echo "CREATE INDEX ON :IDFA (idfa);" | $cmd

echo "CREATE INDEX ON :ADTLogin (eventid);" | $cmd
echo "CREATE INDEX ON :ADTOrder (eventid);" | $cmd
echo "CREATE INDEX ON :ADTPay (eventid);" | $cmd

echo "CREATE INDEX ON :Order (orderid);" | $cmd
echo "CREATE INDEX ON :IPAddr (ipaddr);" | $cmd

echo "CREATE INDEX ON :Country (country_id);" | $cmd
echo "CREATE INDEX ON :Area (area_id);" | $cmd
echo "CREATE INDEX ON :Region (region_id);" | $cmd
echo "CREATE INDEX ON :City (city_id);" | $cmd
echo "CREATE INDEX ON :County (county_id);" | $cmd
echo "CREATE INDEX ON :Isp (isp_id);" | $cmd

