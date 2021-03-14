FROM gitpod/workspace-mysql

RUN echo "if [ ! -e /var/run/mysqld/cloud-init.lock ]; then mysql -uroot -e\"DROP DATABASE IF EXISTS \\\`cloud\\\`;CREATE DATABASE \\\`cloud\\\`;USE \\\`cloud\\\`;CREATE TABLE \\\`payment\\\`(\\\`id\\\` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\\\`serial\\\` varchar(200) DEFAULT '',PRIMARY KEY(\\\`id\\\`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;\"; touch /var/run/mysqld/cloud-init.lock; fi" >> ~/.bashrc

USER root

# Zookeeper
ENV JAVA_HOME "/home/gitpod/.sdkman/candidates/java/current"
ENV ZOOKEEPER_VERSION "3.5.9"
RUN mkdir /opt/zookeeper &&\
    wget http://downloads.apache.org/zookeeper/zookeeper-$ZOOKEEPER_VERSION/apache-zookeeper-$ZOOKEEPER_VERSION-bin.tar.gz -P /opt/zookeeper &&\
    tar zxvf /opt/zookeeper/apache-zookeeper*.tar.gz -C /opt/zookeeper/ &&\
    cp /opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/conf/zoo_sample.cfg /opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/conf/zoo.cfg &&\
    chmod -R 777 /opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/
RUN echo "/opt/zookeeper/apache-zookeeper-$"ZOOKEEPER_VERSION"-bin/bin/zkServer.sh start" > /opt/zookeeper/start.sh &&\
    chmod 777 /opt/zookeeper/start.sh
ENV PATH "$PATH:/opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/bin"

# Consul
ARG CONSUL_VERSION=1.9.4
RUN mkdir /opt/consul &&\
    wget https://releases.hashicorp.com/consul/$CONSUL_VERSION/consul_${CONSUL_VERSION}_linux_amd64.zip -P /opt/consul &&\
    unzip /opt/consul/consul*.zip -d /opt/consul/ &&\
    chmod -R 777 /opt/consul/
ENV PATH "$PATH:/opt/consul"

# Jmeter
ENV JMETER_VERSION "5.4.1"
RUN mkdir /opt/jmeter &&\
    wget https://downloads.apache.org//jmeter/binaries/apache-jmeter-$JMETER_VERSION.zip -P /opt/jmeter &&\
    unzip /opt/jmeter/apache-jmeter*.zip -d /opt/jmeter/ &&\
    chmod -R 777 /opt/jmeter/
ENV CLASSPATH "$CLASSPATH:/opt/jmeter/apache-jmeter-$JMETER_VERSION/lib/ext/ApacheJMeter_core.jar:/opt/jmeter/apache-jmeter-$JMETER_VERSION/lib/jorphan.jar"
ENV PATH "$PATH:/opt/jmeter/apache-jmeter-$JMETER_VERSION/bin"
# More information: https://www.gitpod.io/docs/config-docker/
