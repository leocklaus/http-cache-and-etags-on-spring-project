package io.github.leocklaus.cache_etag_demo.api.controllers;

import io.github.leocklaus.cache_etag_demo.api.dto.PaymentTypeInput;
import io.github.leocklaus.cache_etag_demo.domain.entity.PaymentType;
import io.github.leocklaus.cache_etag_demo.domain.service.PaymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentTypeService paymentTypeService;

    @GetMapping
    public ResponseEntity<List<PaymentType>> getPaymentTypes(){
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl
                        .maxAge(30, TimeUnit.MINUTES)
                        .cachePublic()
                        .immutable())
                .body(paymentTypeService.getAllPaymentTypes());
    }

    @PostMapping
    public ResponseEntity<PaymentType> addPaymentType(PaymentTypeInput input){
        PaymentType paymentType = paymentTypeService.savePaymentType(input);
        URI uri = URI.create("/api/v1/payment");
        return ResponseEntity
                .created(uri)
                .build();
    }

}
