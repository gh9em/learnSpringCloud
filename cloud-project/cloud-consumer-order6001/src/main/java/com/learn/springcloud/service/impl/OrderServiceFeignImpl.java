package com.learn.springcloud.service.impl;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.service.OrderService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "CLOUD-PAYMENT-SERVICE", fallback = OrderServiceFeignFallback.class)
@Service
public interface OrderServiceFeignImpl extends OrderService {
    @PostMapping("/payment/create")
    public CommonResult<Integer> create(@RequestBody Payment payment);

    @GetMapping("/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id);

    @GetMapping("/payment/slow/{id}")
    public CommonResult<Payment> slowGetPaymentById(@PathVariable("id") Long id);
}
