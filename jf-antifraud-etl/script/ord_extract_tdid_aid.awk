#!/usr/bin/awk -f

BEGIN {
  FS = "\t";
  APPKEY_AND = "57497567ae9d4351a3c306a7f6267213";
  printf("tdid,androidid\n");
}
NR >= 2 {
  orderid = $6;
  ip = $5;
  amount = $7;

  tdid = $1;
  aid = $4;
  appkey = $9;

  if (orderid != "" && ip ~ /^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$/) {
    if (appkey == APPKEY_AND && aid != "") {
      printf("%s,%s\n", tdid, aid);
    }
  }
}

