#!/usr/bin/awk -f

BEGIN {
  FS = "\t";
  APPKEY_IOS = "5409a87f301e4c438149be1176517583";  
  printf("tdid,idfa\n");
}
NR >= 2 {
  tdid = $1;
  account = $5;
  idfa = $2;
  appkey = $7;

  if (account ~ /^[0-9]{5,7}$/ || account ~ /^1[3|4|5|7|8][0-9]{9}$/) {
    if (appkey == APPKEY_IOS && idfa != "") {
      printf("%s,%s\n", tdid, idfa);
    }
  }
}

