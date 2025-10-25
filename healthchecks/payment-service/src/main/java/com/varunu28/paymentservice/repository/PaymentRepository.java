package com.varunu28.paymentservice.repository;

import com.varunu28.paymentservice.model.Payment;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, UUID> {
}
