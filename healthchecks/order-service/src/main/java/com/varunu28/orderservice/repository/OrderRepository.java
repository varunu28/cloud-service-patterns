package com.varunu28.orderservice.repository;

import com.varunu28.orderservice.model.Order;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> {
}
