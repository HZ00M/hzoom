#ik分词器
wget https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.4.0/elasticsearch-analysis-ik-7.4.0.zip

#解压
unzip elasticsearch-analysis-ik-7.4.0.zip -d ik/

#复制进容器
docker cp ik/ es01:/usr/share/elasticsearch/plugins/
docker cp ik/ es02:/usr/share/elasticsearch/plugins/