# LearnSpringCloud

[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/gh9em/learnSpringCloud)

# Parent Project

1. generate project template
    ```
    mvn archetype:generate 
    -DgroupId=com.learn.springcloud 
    -DartifactId=cloud-project 
    -Dversion=1.0-SNAPSHOT 
    -DarchetypeGroupId=org.apache.maven.archetypes 
    -DarchetypeArtifactId=maven-archetype-site 
    -DarchetypeVersion=RELEASE
    ```

2. delete `cloud-project/src` & modify `pom.xml`

    + add property `<packaging>pom</packaging>` (means pom parent project)
    + add plugin `spring-boot-maven-plugin`
    + provide optional sub-projects dependencies groupId&version
    + provide optional sub-projects plugins groupId&version

# Sub Project

1. generate project template
    ```
    mvn archetype:generate 
    -DgroupId=com.learn.springcloud 
    -DartifactId=xxx
    -Dversion=1.0-SNAPSHOT 
    -DarchetypeGroupId=org.apache.maven.archetypes 
    -DarchetypeArtifactId=maven-archetype-quickstart 
    -DarchetypeVersion=RELEASE
    ```
2. modify `pom.xml`

    add dependencies
3. create `application.yml`&Main class
4. create table
    ```sql
    CREATE DATABASE `cloud`;
    USE `cloud`;
    CREATE TABLE `payment`(
        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
        `serial` varchar(200) DEFAULT '',
        PRIMARY KEY(`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
    ```
# Registry Center
## Eureka Cluster (AP)
### Eureka Server Principle
    |-Eureka Server--------------------------------------------------------------------------------|
    |         |----------------|            |---------------------|            |------------------||
    <<export<<|readOnlyCacheMap|<<interval<<|  readWriteCacheMap  |<<interval<<|     registry     ||
    <<outside<|(ConcurrentMap) |<<refresh<<<|((Guava)LoadingCache)|<<refresh<<<|(InstanceRegistry)||
    |         |----------------|            |---------------------|            |------------------||
    |----------------------------------------------------------------------------------------------|

### Eureka Cluster Principle
    |-Eureka Cluster----------------------------------------|
    | Regin                                                 |
    |   |--------------------------------------------------||
    |   | Zone            |-------------|                  ||
    |   |                 |Eureka Server||                 ||
    |   |                 |-------------||                 ||
    |   |                  |-------------|                 ||
    |   |   reg、renew↗↙fetch    reg、renew↖↘fetch      ||
    |   | |---------------|     rpc     |---------------|  ||
    |   | |Consumer Client|>>>>>>>>>>>>>|Provider Server|| ||
    |   | |---------------|             |---------------|| ||
    |   |                                |---------------| ||
    |   |                                                  ||
    |   |--------------------------------------------------||
    |-------------------------------------------------------|

### Usage
1. generate eureka server project template(same as sub project)
2. modify `pom.xml`

    + add dependencies
        + server: `spring-cloud-starter-netflix-eureka-server`
        + client: `spring-cloud-starter-netflix-eureka-client`

3. create `application.yml`&Main class
    
    + set `spring.application.name`
    + add eureka config
        ```yaml
        eureka:
            instance:
                # unique or config unique 'eureka.server.my-url'(see https://www.cnblogs.com/lonelyJay/p/9940199.html, https://blog.csdn.net/ai_xao/article/details/102516384)
                hostname: localhost
                # instance timeout duration
                # lease-expiration-duration-in-seconds: 90
                # instance keep-alive interval time
                # lease-renewal-interval-in-seconds: 3
            server:
                my-url: http://localhost:7001/eureka/
                # do not remove no-response instances(if more than 85%) immediately
                # enable-self-preservation: true
                # server check is instances timeout interval time
                # eviction-interval-timer-in-ms: 60000
            #-----↓server & client↓-----
            client:
                # set true for replicas available test
                # register-with-eureka: false
                # set true for replicas available test
                # fetch-registry: false
                # value type is map, do not change key name, such as 'defaultZone'
                service-url:
                # single: url->self cluster: url->any-other
                defaultZone: http://localhost:7002/eureka/
        ```
    + add annotations
        + server: `@EnableEurekaServer`
        + client: `@EnableEurekaClient`

4. load balance
    
    add annotation **`@LoadBalanced`** when registry `RestTemplate` in consumer client

## Zookeeper Cluster (CP)
### Zookeeper Server Principle
host type:
+ Leader (join (ZAB)Paxos-leader/write-campaign: more than 1/2 agree)
+ Learner
    + Follower (join (ZAB)Paxos-leader/write-campaign)
    + Observer (no join (ZAB)Paxos-leader/write-campaign)

### Zookeeper Cluster Principle
                        |-----------------|
                        |Zk Leader/Learner||
                        |-----------------||
                         |-----------------|
     watch EPHEMERAL↗↙                      ↖↘watch EPHEMERAL
        |---------------|       rpc        |---------------|
        |Consumer Client|>>>>>>>>>>>>>>>>>>|Provider Server||
        |---------------|                  |---------------||
                                            |---------------|

### Usage
1. rename `zookeeper/conf/zoo_sample.cfg` to `zoo.cfg`
2. modify `zoo.cfg`

    + set property `dataDir`
    + add property `server.n=ip:x:y`
        + `n` means `hostid`, range is `[1, 255]`
        + `x` means leader-data-exchange-port
        + `y` means campaign-port

3. mkdir `dataDir`
4. echo `hostid` > `dataDir/mypid`
5. modify `pom.xml`

    + add dependencies `spring-cloud-starter-zookeeper-discovery`

6. create `application.yml`&Main class
    
    + set `spring.application.name`
    + add cloud config
        ```yaml
        spring:
            cloud:
                zookeeper:
                    # url1,url2,...
                    connect-string: localhost:2181
        ```
    + add annotation `@EnableDiscoveryClient`

7. load balance
    
    add annotation **`@LoadBalanced`** when registry `RestTemplate` in consumer client
