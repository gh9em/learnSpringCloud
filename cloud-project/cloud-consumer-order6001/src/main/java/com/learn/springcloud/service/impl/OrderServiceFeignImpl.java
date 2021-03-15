package com.learn.springcloud.service.impl;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("CLOUD-PAYMENT-SERVICE")
@Service
public interface OrderServiceFeignImpl extends OrderService {
    @PostMapping("/payment/create")
    public CommonResult<Integer> create(@RequestBody Payment payment);

    @GetMapping("/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id);

    @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    @GetMapping("/payment/slow/{id}")
    public CommonResult<Payment> slowGetPaymentById(@PathVariable("id") Long id);

    public default CommonResult<Payment> fallback() {
        return new CommonResult<>(500, "hystrix fallback");
    }
}
