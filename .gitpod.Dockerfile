FROM gitpod/workspace-mysql

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

# Consul
ARG CONSUL_VERSION=1.9.4
RUN mkdir /opt/consul &&\
    wget https://releases.hashicorp.com/consul/$CONSUL_VERSION/consul_${CONSUL_VERSION}_linux_amd64.zip -P /opt/consul &&\
    unzip /opt/consul/consul*.zip -d /opt/consul/ &&\
    chmod -R 777 /opt/consul/
# More information: https://www.gitpod.io/docs/config-docker/
