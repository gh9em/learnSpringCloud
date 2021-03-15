package com.learn.springcloud.service;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;

public interface OrderService {
    public CommonResult<Integer> create(Payment payment);

    public CommonResult<Payment> getPaymentById(Long id);

    public CommonResult<Payment> slowGetPaymentById(Long id);
}
