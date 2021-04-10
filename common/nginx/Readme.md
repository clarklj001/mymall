## Set project path in 

~~~
PROJECT_COMMON_PATH=~/projects/tutorials/mymall/common
~~~

## Create and run a nginx server and export the config information

~~~

cd $PROJECT_COMMON_PATH

mkdir nginx

cd nginx

sudo docker run -p 6666:80 --name nginx --network mymall -d nginx:1.19

sudo docker cp nginx:/etc/nginx .

sudo mv nginx conf


~~~

## Remove the old nginx and create a new nginx server with configuration

~~~

sudo docker container rm nginx -f

sudo docker run -p 80:80 --name nginx --network mymall \
-v $PROJECT_COMMON_PATH/nginx/html:/usr/share/nginx/html \
-v $PROJECT_COMMON_PATH/nginx/logs:/var/log/nginx \
-v $PROJECT_COMMON_PATH/nginx/conf:/etc/nginx \
-d nginx:1.19

~~~

## Test nginx in browser

localhost:6666
