#!/usr/bin/awk -f

BEGIN {
  FS = "\t";
  printf("event,time,ts,tdid,account\n");
}
NR >= 2 {
  tdid = $1;
  account = $5;
  txt = $0;
  eventTime = $6;


  year = substr(eventTime, 0, 4);
  month = substr(eventTime, 6, 2);
  day = substr(eventTime, 9, 2);

  hour = substr(eventTime, 12, 2);
  minute = substr(eventTime, 15, 2);
  second = substr(eventTime, 18, 2);

  if (account ~ /^[0-9]{5,7}$/ || account ~ /^1[3|4|5|7|8][0-9]{9}$/) {

    # calc the md5 for current text line
    # cmd = sprintf("echo %s | md5sum", txt);
    # cmd | getline x;
    
    # split(x, b, " ");
    # md5 = b[1];
    ts = mktime(sprintf("%s %s %s %s %s %s", year, month, day, hour, minute, second));
    evt = sprintf("%s-%s-%s", tdid, account, ts);

    printf("%s,%s,%s000,%s,%s\n", evt, eventTime, ts, tdid, account);
  }
}


