package com.learn.springcloud.controller;

import com.learn.springcloud.entities.CommonResult;
import com.learn.springcloud.entities.Payment;
import com.learn.springcloud.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/create")
    public CommonResult<Integer> create(Payment payment) {
        int result = paymentService.create(payment);
        Log.info("create result:" + result);
        if (result > 0)
            return new CommonResult<>(200, "success", result);
        else
            return new CommonResult<>(444, "fail");
    }

    @GetMapping("/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        Payment result = paymentService.getPaymentById(id);
        Log.info("create result:" + result);
        if (result != null)
            return new CommonResult<>(200, "success", result);
        else
            return new CommonResult<>(444, "fail id-" + id);

    }
}