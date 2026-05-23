package com.saga.inventoryservice

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface InventoryRecordRepository : CrudRepository<InventoryRecord, BigInteger>