package com.varunu28.paymentservice.service;

import com.varunu28.paymentservice.exception.DuplicateIdempotencyException;
import com.varunu28.paymentservice.exception.PaymentNotFoundException;
import com.varunu28.paymentservice.model.Payment;
import com.varunu28.paymentservice.repository.PaymentRepository;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment findById(UUID id) throws PaymentNotFoundException {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
    }

    public UUID createPayment(String idempotencyKey, double amount, UUID customerId)
        throws DuplicateIdempotencyException {
        Payment payment = new Payment(idempotencyKey, amount, customerId);
        try {
            Payment savedPayment = paymentRepository.save(payment);
            return savedPayment.getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateIdempotencyException();
        }
    }
}
