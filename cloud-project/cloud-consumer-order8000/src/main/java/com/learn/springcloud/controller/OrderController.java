package com.learn.springcloud.controller;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OrderController {
    private static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";
    
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/payment/create")
    public CommonResult<Integer> create(Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, CommonResult.class);
    }

    @GetMapping("/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PAYMENT_URL + "/payment/" + id, CommonResult.class);
    }
}
