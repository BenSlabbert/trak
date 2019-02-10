FROM docker.elastic.co/logstash/logstash:6.4.1

RUN logstash-plugin install logstash-input-jdbc

COPY products-import.conf /usr/share/logstash/pipeline/products-import.conf
COPY brands-import.conf /usr/share/logstash/pipeline/brands-import.conf
COPY category-import.conf /usr/share/logstash/pipeline/category-import.conf

COPY pipelines.yml /usr/share/logstash/config/pipelines.yml
COPY logstash.yml /usr/share/logstash/config/logstash.yml

COPY mysql-connector.jar /usr/share/mysql-connector.jar
