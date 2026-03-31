package com.varunu28.orderservice.repository;

import com.varunu28.orderservice.model.Outbox;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends CrudRepository<Outbox, Long> {
}
