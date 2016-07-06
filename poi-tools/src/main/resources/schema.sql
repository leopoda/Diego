drop table if exists address;

create table address (
  id identity,
  tdid varchar(255) not null,
  month varchar(7) not null,
  hour varchar(2) not null,
  lng double,
  lat double,
  country varchar(255),
  province varchar(255),
  city varchar(255),
  district varchar(255),
  township varchar(255),
  address varchar(255),
  isWeekend bool,
  count int
);