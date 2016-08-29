#!/bin/bash

./creat_idx.sh
./load_dmp_reg_csv.sh
./load_dmp_login_csv
./load_dmp_order_csv.sh
./load_dmp_pay_csv.sh
./load_ip_loc.sh
