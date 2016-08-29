#!/usr/bin/awk -f

BEGIN {
  FS = "\t";
  printf("tdid,mac\n");
}
NR >= 2 {
  tdid = $1;
  mac = $3;
  account = $5;

  if (account ~ /^[0-9]{5,7}$/ || account ~ /^1[3|4|5|7|8][0-9]{9}$/) {
    if ((mac ~ /^[A-F0-9]{2}(:[A-F0-9]{2}){5}$/ || mac != "") && mac != "02:00:00:00:00:00") {
      printf("%s,%s\n", tdid, mac);
    }
  }
}

