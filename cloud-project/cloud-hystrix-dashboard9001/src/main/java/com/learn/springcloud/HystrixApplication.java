package com.learn.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@EnableHystrixDashboard
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HystrixApplication {
    public static void main( String[] args ) {
        SpringApplication.run(HystrixApplication.class, args);
    }
}
