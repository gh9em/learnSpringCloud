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

4. Ribbon load balance
    
    add annotation **`@LoadBalanced`** when registry `RestTemplate` in **consumer** client

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

7. Ribbon load balance
    
    add annotation **`@LoadBalanced`** when registry `RestTemplate` in **consumer** client

## Consul Cluster (CP)
### Consul Server Principle
host type (set by command option `agent`):
+ Server (join Raft-leader/write-campaign: more than 1/2 agree)
+ Client (no join Raft-leader/write-campaign)

> Raft-leader/write-campaign see: https://www.jianshu.com/p/8e4bbe7e276c

### Consul Cluster Principle
    |-Consul Cluster-----------------------------------------|
    | DataCenter      |-------------|                        |
    |                 |Consul Server||                       |
    |                 |-------------||                       |
    |         ↗Raft↙  |-------------| ↖Raft↘              |
    |   |-------------|              |-------------|         |
    |   |Consul Client|              |Consul Client||        |
    |   |-------------|              |-------------||        |
    |          ↑  ↓                   |-------------|        |
    | heartbeat↑or↓health-check    heartbeat↑or↓health-check |
    | |---------------|     rpc     |---------------|        |
    | |Consumer Client|>>>>>>>>>>>>>|Provider Server||       |
    | |---------------|             |---------------||       |
    |                                |---------------|       |
    |                                                        |
    |--------------------------------------------------------|

### Usage
1. run commands
    + server: `consul agent -server -bootstrap-expect=n -data-dir=data -node=nodename -bind=ip -client=ip -datacenter=dc1 -ui`
        + `n` means server replicas count
        + `-bind=ip` means data exchange ip
        + `-client=ip` means client(HTTP/DNS/WebUI) access ip
    + client: `consul agent -data-dir=data -datacenter=dc1`
    + server&client: `consul join ip` (instead of add option `-join=ip` at start time)
        + `ip` means any cluster node ip

2. modify `pom.xml`

    + add dependencies `spring-cloud-starter-consul-discovery`

3. create `application.yml`&Main class
    
    + set `spring.application.name`
    + add cloud config
        ```yaml
        spring:
          cloud:
            consul:
              # consul client host
              host: localhost
              # consul client port
              port: 8500
              discovery:
                service-name: ${spring.application.name}
                prefer-ip-address: true
                # active keep-alive by self
                # heartbeat.enabled: true
                # passive keep-alive by server
                register-health-check: true
                health-check-interval: 10s
                health-check-path: /actuator/health
        ```
    + add annotation `@EnableDiscoveryClient`

4. Ribbon load balance
    
    add annotation **`@LoadBalanced`** when registry `RestTemplate` in **consumer** client

# Outside Load Balance(HA)
Nginx
# Inside Load Balance(HA)
## Ribbon + *RestTemplate*
### Ribbon Principle
1. choose less-use server from `serverList` which updated by:
    + schedule task `PingTask` (only support Eureka by default)
    + schedule task's `UpdateAction`
    + Eureka notification watcher's `UpdateAction`
2. choose any node of service name by rule:
    + RoundRobin(default, retry count is 10): round call
        + Retry: retry round call if no-response until `maxRetryMillis`
        + WeightedResponseTime: response speed weight round call
    + Random(dead-loop is always no-response)
    + BestAvailableRule
    + AvailabilityFiltering
    + ZoneAvoidance
    + PredicateBasedRule(abstract for Guava)
> see https://zhuanlan.zhihu.com/p/262660637, https://www.jianshu.com/p/1bd66db5dc46

### Usage
1. modify `pom.xml`

    + add dependencies `spring-cloud-starter-netflix-ribbon`

2. create `application.yml`&Main class
    + add annotation `@RobbinClient(name = "provider-service-name", configuration = CustomRule.class)` if needed
        + `provider-service-name` must equals `RestTemplate` request uri
        + class `CustomRule` **must not define in `@ComponentScan` reaches package**
    + add ribbon config
        ```yaml
        # provider-service-name must equals `RestTemplate` request uri, if not set means global ribbon config
        provider-service-name:
          ribbon:
            connectTimeout: 2000
            readTimeout: 5000
            # retry enable
            # OkToRetryOnAllOperations: true
            # each node retry count
            # MaxAutoRetries: 0
            # retry node count
            # MaxAutoRetriesNextServer: 1
        ```
    > see http://c.biancheng.net/view/5356.html

3. create `CustomRule` class if needed
4. Ribbon load balance
    
    add annotation **`@LoadBalanced`** when registry `RestTemplate` in **consumer** client

# REST Client
## (Open)Feign + *Ribbon*
### (Open)Feign Principle
    |-Consumer Client------------------------------|
    |     @FeignClient                             |
    |  Java↓Dynamic↑Proxy                          |
    | RestTemplate ↑                               |
    |      ↓       ↑                               |
    |    Feign.Client                              |
    |      ↓       ↑                               |
    |    Hystrix's CircuitBreaker(default disable) |
    |      ↓       ↑                               |
    |    Robbin's LoadBalance                      |
    |----------------------------------------------|
           ↓       ↑
        |---------------|
        |Provider Server||
        |---------------||
         |---------------|

### Usage
1. modify `pom.xml`

    + add dependencies `spring-cloud-starter-openfeign`

2. create `application.yml`&Main class
    + add annotation `@EnableFeignClients`
    + add annotation `@EnableEurekaClient` if needed
    + add discovery center config, such as `Eureka`
    + add feign config
        ```
        feign:
          client:
            config:
              # provider-service-name, default means global; or use feignName: 'provider-service-name' instead
              default:
                  connectTimeout: 5000
                  readTimeout: 5000
                  # none(default), basic, headers, full
                  # loggerLevel: none
        ```

3. (Open)Feign
    + add annotation **`@FeignClient("provider-service-name")`** on **consumer** client's service
        > also can add a simple `hystrix` fallback/fallbackFactory to generate self-response if add config
        >    ```
        >    feign:
        >      hystrix:
        >        enabled: true
        >    ```
    + add annotation `@GetMapping` or `@PostMapping` on **consumer** client's service

# Fallback↥ + CircuitBreaker¦+ FlowLimiter⇃
## Hystrix
### Hystrix Principle
    HystrixCommand     HystrixObservableCommand
     ↓          ↓               (RxJava)
     ↓          ↓               ↓     ↓↑
    execute()>>queue()>>>>>observe()>>toObservable()
                                      ↓↑Y     ↑ ↑
                                    |-----|   ↑ ↑
                                    |Cache|   ↑ ↑
                                    |-----|   ↑ ↑
                                      ↓↑     getFallback()/resumeWithFallback()
               Health Info |--------------|N  ↑ ↑
                  >·>·>·>·>|CircuitBreaker|>>>¦ ↑
                  ↑        |--------------|     ↑
                  ↑                   ⇃↑Y       ↑
                  ↑  |--------------------|N    ↑
                  ↑·<|Samephore/ThreadPool|>>>>>↥
         Health Info |--------------------|

### CircuitBreaker principle
    -----↓Y          ---↓Y/N
    ↑    ↓           ↑  ↓    Sleep
    CLOSED>>>>>>>>>>>OPEN>>>>>>>>>>>Half-OPEN
         ↑              ↑          Y↓       ↓N
         ↑              ↑------------       ↓
         ↑                                  ↓
         ↑-----------------------------------

> also can see https://www.github.com/Netflix/Hystrix/wiki/How-it-Works, https://yq.aliyun.com/articles/679587

### Usage
1. modify `pom.xml`

    + add dependencies `spring-cloud-starter-netflix-hystrix`

2. create `application.yml`&Main class
    + add annotation `@EnableCircuitBreaker`
    + add annotation `@EnableEurekaClient`
    + add eureka config
    + add feign config if needed
        ```
        feign:
          hystrix:
            enabled: true

        hystrix:
          command:
            # (Open)feign will generate 'className#methodName(param1Type,param2Type,...)' for each api by default, see https://github.com/OpenFeign/feign/blob/master/hystrix/README.md
            # commandKey, default means global
            default:
              execution.isolation:
                # samephore(immediately reject), thread(thread pool queue)
                # strategy: thread
                thread.timeoutInMilliseconds: 2000
              circuitBreaker:
                enabled: true
                requestVolumeThreshold: 10
                # if (requestVolumeThreshold * errorThresholdPercentage% = )5 requests fail circuitBreaker change to OPEN status in each 10 requests
                errorThresholdPercentage: 50
                # sleep time of circuitBreaker change from OPEN to Half-OPEN status
                sleepWindowInMilliseconds: 10000
        ```

3. Hystrix
    + add annotation `@HystrixCommand(fallbackMethod="fallback-method-name", commandProperties = {@HystrixProperty(name = "prop-key", value = "prop-value"),...})` on **consumer** client's serivce if needed

4. ~~Hystrix Dashboard~~
    + ~~add dependencies `spring-cloud-starter-netflix-hystrix-dashboard`~~
    + ~~add annotation `@EnableHystrixDashboard` in Main class~~

> Stress Test: `jmeter -n -t cloud.jmx -l test.jtl`

# Service Gateway
## Zuul
## Spring-Cloud-Gateway
### Spring-Cloud-Gateway Principle
     |-Spring-Cloud-Gateway(Netty)----------------------------------|
     |  |---------------|  |-----------|  |---------------------|   | |-------------------------------|
    >>>>|    Gateway    |>>|  Gateway  |>>|Pre/Post Filter Chain||>>>>|Consumer Client/Provider Server||
    <<<<|Handler Mapping|<<|Web Handler|<<|---------------------||<<<<|-------------------------------||
     |  |---------------|  |-----------|   |---------------------|  |  |-------------------------------|
     |--------------------------------------------------------------|
