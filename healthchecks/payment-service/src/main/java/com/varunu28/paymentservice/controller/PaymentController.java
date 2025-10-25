package com.varunu28.paymentservice.controller;

import com.varunu28.paymentservice.dto.CreatePaymentDto;
import com.varunu28.paymentservice.dto.PaymentDto;
import com.varunu28.paymentservice.exception.DuplicateIdempotencyException;
import com.varunu28.paymentservice.exception.PaymentNotFoundException;
import com.varunu28.paymentservice.model.Payment;
import com.varunu28.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/payments", produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> createPayment(@Valid @RequestBody CreatePaymentDto createPaymentDto)
        throws DuplicateIdempotencyException {
        UUID paymentId = paymentService.createPayment(
            createPaymentDto.idempotencyKey(),
            createPaymentDto.amount(),
            createPaymentDto.customerId()
        );
        return ResponseEntity.ok(paymentId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable UUID id) throws PaymentNotFoundException {
        Payment byId = paymentService.findById(id);
        return ResponseEntity.ok(PaymentDto.fromPayment(byId));
    }
}
