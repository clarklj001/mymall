## Set project path in 

~~~
PROJECT_COMMON_PATH=~/projects/tutorials/mymall/common
~~~

## Create and run a elasticsearch container

~~~
sudo docker network create -d bridge mymall
sudo docker pull elasticsearch:7.12.0
sudo docker pull kibana:7.12.0
cd $PROJECT_COMMON_PATH/elasticsearch

mkdir config
mkdir data
mkdir plugins
echo "http.host: 0.0.0.0" >> config/elasticsearch.yml

sudo docker run -p 9200:9200 -p 9300:9300 --network mymall --name elasticsearch \
-e "discovery.type=single-node" \
-e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
-v $PROJECT_COMMON_PATH/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v $PROJECT_COMMON_PATH/elasticsearch/data:/usr/share/elasticsearch/data \
-v $PROJECT_COMMON_PATH/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.12.0
~~~

## Test ElasticSearch in browser

localhost:9200

## Run kibana
~~~

sudo docker run --name kibana --network mymall -p 5601:5601 -d kibana:7.12.0

~~~

## Test Kibana in browser

localhost:5601
