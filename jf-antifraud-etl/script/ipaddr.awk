#!/usr/bin/awk -f

BEGIN {
  FS = ",";
  UNKNOWN_ID = "0";
  UNKNOWN_NAME = "未知";
  printf("ip,country_id,country,area_id,area,region_id,region,city_id,city,county_id,county,isp_id,isp\n");
}
{
  ip = $1;

  country_id = $2;
  country = $3;

  area_id = $4;
  area = $5;

  region_id = $6;
  region = $7;
  
  city_id = $8;
  city = $9;
  
  county_id = $10;
  county = $11;
  
  isp_id = $12;
  isp = $13;
  
  dummy = $14;
  
  v_area_id = UNKNOWN_ID;
  v_region_id = UNKNOWN_ID;
  v_city_id = UNKNOWN_ID;
  v_county_id = UNKNOWN_ID;
  v_isp_id = UNKNOWN_ID;

  v_area = UNKNOWN_NAME;
  v_region = UNKNOWN_NAME;
  v_city = UNKNOWN_NAME;
  v_county = UNKNOWN_NAME;
  v_isp = UNKNOWN_NAME;
  
  if (ip ~ /^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$/) {

    # country_id is always available
    # printf("%s,", country_id);

    if (area_id != "" && area_id != "-1") {
      v_area_id = area_id;
    } else {
      v_area_id = sprintf("%s-%s", country_id, UNKNOWN_ID);
    }

    if (area != "") {
      v_area = area;
    }

    if (region_id != "" && region_id != "-1") {
      v_region_id = region_id;
    } else {
      v_region_id = sprintf("%s-%s", v_area_id, UNKNOWN_ID);
    }

    if (region != "") {
      v_region = region;
    }

    if (city_id != "" && city_id != "-1") {
      v_city_id = city_id;
    } else {
      v_city_id = sprintf("%s-%s", v_region_id, UNKNOWN_ID);
    }

    if (city != "") {
      v_city = city;
    }

    if (county_id != "" && county_id != "-1") {
      v_county_id = county_id;
    } else {
      v_county_id = sprintf("%s-%s", v_city_id, UNKNOWN_ID);
    }

    if (county != "") {
      v_county = county;
    }

    if (isp_id != "" && isp_id != "-1") {
      v_isp_id = sprintf("%s-%s", v_county_id, isp_id);
    } else {
      v_isp_id = sprintf("%s-%s", v_county_id, UNKNOWN_ID);
    }

    if (isp != "") {
      v_isp = isp;
    }

    printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", ip, country_id, country, v_area_id, v_area, v_region_id, v_region, v_city_id, v_city, v_county_id, v_county, v_isp_id, v_isp);
  }
}

