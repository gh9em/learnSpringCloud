package com.learn.springcloud.service.impl;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;

import org.springframework.stereotype.Component;

@Component
public class OrderServiceFeignFallback implements OrderServiceFeignImpl {

    @Override
    public CommonResult<Integer> create(Payment payment) {
        return new CommonResult<>(500, "feign-hystrix fallback");
    }

    @Override
    public CommonResult<Payment> getPaymentById(Long id) {
        return new CommonResult<>(500, "feign-hystrix fallback");
    }

    @Override
    public CommonResult<Payment> slowGetPaymentById(Long id) {
        return new CommonResult<>(500, "feign-hystrix fallback");
    }

}
