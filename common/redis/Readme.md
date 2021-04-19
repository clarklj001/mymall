## Set project path

~~~

PROJECT_COMMON_PATH=~/projects/tutorials/mymall/common

~~~

## Create and run a redis container

~~~

mkdir -p $PROJECT_COMMON_PATH/redis/conf

touch $PROJECT_COMMON_PATH/redis/conf/redis.conf

sudo docker run -p 6379:6379 --name redis --network mymall \
-v $PROJECT_COMMON_PATH/redis/conf/redis.conf:/etc/redis/redis.conf \
-v $PROJECT_COMMON_PATH/redis/data:/data \
-d redis redis-server /etc/redis/redis.conf


~~~

## Run redis client console

~~~
sudo docker exec -it redis redis-cli
~~~


## Install Redis Desktop Manager (UI)

~~~
sudo apt update
sudo apt install snapd
sudo snap install redis-desktop-manager
~~~