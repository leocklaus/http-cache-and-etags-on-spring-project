package io.github.leocklaus.cache_etag_demo.domain.service;

import io.github.leocklaus.cache_etag_demo.api.dto.PaymentTypeInput;
import io.github.leocklaus.cache_etag_demo.domain.entity.PaymentType;
import io.github.leocklaus.cache_etag_demo.domain.repository.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    public PaymentType savePaymentType(PaymentTypeInput paymentTypeInput){
        PaymentType paymentType = PaymentType.builder()
                .name(paymentTypeInput.name())
                .build();
        return paymentTypeRepository.save(paymentType);

    }

    public List<PaymentType> getAllPaymentTypes(){
        return paymentTypeRepository.findAll();
    }

}
