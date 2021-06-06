package com.learn.springcloud.controller;

import java.util.List;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@DefaultProperties(defaultFallback = "globalFallback")
@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/payment/create")
    public CommonResult<Integer> create(Payment payment) {
        return orderService.create(payment);
    }

    @GetMapping("/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        return orderService.getPaymentById(id);
    }

    @HystrixCommand(/*fallbackMethod = "slowGetPaymentByIdFallback", */commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    @GetMapping("/payment/slow/{id}")
    public CommonResult<Payment> slowGetPaymentById(@PathVariable("id") Long id) {
        return orderService.slowGetPaymentById(id);
    }

    @GetMapping("/instances")
    public List<ServiceInstance> getInstances() {
        return discoveryClient.getInstances(applicationName);
    }

    public CommonResult<Payment> slowGetPaymentByIdFallback(Long id) {
        return new CommonResult<>(505, "hystrix fallback id-" + id);
    }

    public CommonResult<Payment> globalFallback() {
        return new CommonResult<>(506, "hystrix global fallback");
    }
}
