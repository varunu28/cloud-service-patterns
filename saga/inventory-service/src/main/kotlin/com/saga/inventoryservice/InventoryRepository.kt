package com.saga.inventoryservice

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface InventoryRepository : CrudRepository<Inventory, BigInteger> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByOrderId(orderId: BigInteger): Inventory?
}