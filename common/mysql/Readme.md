## Set project path in 

~~~
PROJECT_COMMON_PATH=~/projects/tutorials/mymall/common
~~~

## Create and run a mysql container

~~~
sudo docker run -p 3306:3306 --name mysql --network mymall \
-v $PROJECT_COMMON_PATH/mysql/log:/var/log/mysql \
-v $PROJECT_COMMON_PATH/mysql/data:/var/lib/mysql \
-v $PROJECT_COMMON_PATH/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7
~~~

## Connect database with UI Tool (sqlectron)

## Create databases

create_db.sql

## Create tables

gulimall_oms.sql

gulimall_pms.sql

gulimall_sms.sql

gulimall_ums.sql

gulimall_wms.sql
