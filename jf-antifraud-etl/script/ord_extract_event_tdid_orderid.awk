#!/usr/bin/awk -f

BEGIN {
  FS = "\t";
  printf("event,time,ts,amt,tdid,oid,ip\n");
}
NR >= 2 {
  tdid = $1;
  orderid = $6;
  eventTime = $8;
  ip = $5;
  amount = $7;

  year = substr(eventTime, 0, 4);
  month = substr(eventTime, 6, 2);
  day = substr(eventTime, 9, 2);

  hour = substr(eventTime, 12, 2);
  minute = substr(eventTime, 15, 2);
  second = substr(eventTime, 18, 2);

  if (orderid != "" && ip ~ /^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$/) {

    ts = mktime(sprintf("%s %s %s %s %s %s", year, month, day, hour, minute, second));
    evt = sprintf("%s-%s-%s", tdid, orderid, ts);

    printf("%s,%s,%s000,%s,%s,%s,%s\n", evt, eventTime, ts, amount, tdid, orderid, ip);
  }
}

