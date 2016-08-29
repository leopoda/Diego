#!/usr/bin/awk -f

BEGIN {
  FS = "\t";
  APPKEY_AND = "57497567ae9d4351a3c306a7f6267213";
  printf("tdid,androidid\n");
}
NR >= 2 {
  tdid = $1;
  account = $5;
  aid = $4;
  appkey = $7;

  if (account ~ /^[0-9]{5,7}$/ || account ~ /^1[3|4|5|7|8][0-9]{9}$/) {
    if (appkey == APPKEY_AND && aid != "") {
      printf("%s,%s\n", tdid, aid);
    }
  }
}

