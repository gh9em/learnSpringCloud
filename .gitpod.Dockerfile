FROM gitpod/workspace-mysql

USER root
# Install custom tools, runtimes, etc.
# For example "bastet", a command-line tetris clone:
# RUN brew install bastet
ENV JAVA_HOME "/home/gitpod/.sdkman/candidates/java/current"
ENV ZOOKEEPER_VERSION "3.5.9"

RUN mkdir /opt/zookeeper &&\
    wget http://downloads.apache.org/zookeeper/zookeeper-$ZOOKEEPER_VERSION/apache-zookeeper-$ZOOKEEPER_VERSION-bin.tar.gz -P /opt/zookeeper &&\
    tar zxvf /opt/zookeeper/apache-zookeeper*.tar.gz -C /opt/zookeeper/ &&\
    cp /opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/conf/zoo_sample.cfg /opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/conf/zoo.cfg &&\
    chmod -R 777 /opt/zookeeper/apache-zookeeper-$ZOOKEEPER_VERSION-bin/

RUN echo "/opt/zookeeper/apache-zookeeper-$"ZOOKEEPER_VERSION"-bin/bin/zkServer.sh start" > /opt/zookeeper/start.sh &&\
    chmod 777 /opt/zookeeper/start.sh

EXPOSE 2181
# More information: https://www.gitpod.io/docs/config-docker/
