package com.saga.inventoryservice

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository,
    private val inventoryRecordRepository: InventoryRecordRepository
) {

    @Value($$"${inventory.count.threshold}")
    private var inventoryCountThreshold: Int = 1000

    @Transactional
    fun persistInventory(
        customerName: String,
        inventoryCount: Int,
        orderId: BigInteger,
    ) {
        if (inventoryCount > inventoryCountThreshold) {
            throw InventoryExceededException()
        }
        val now = LocalDateTime.now()
        val inventory = Inventory(
            customerName = customerName,
            orderId = orderId,
            inventoryCount = inventoryCount,
            createdAt = now,
            updatedAt = now,
            status = "RESERVED"
        )
        val savedInventory = inventoryRepository.save(inventory)
        val inventoryId = savedInventory.id ?: BigInteger.ZERO
        val inventoryRecord = InventoryRecord(
            inventoryId = inventoryId,
            recordType = "INVENTORY_ADDED",
            inventoryCount = inventoryCount,
            createdAt = now
        )
        inventoryRecordRepository.save(inventoryRecord)
    }

    @Transactional
    fun revertInventory(orderId: BigInteger) {
        // Query inventory
        val inventory = inventoryRepository.findByOrderId(orderId = orderId) ?: return
        val now = LocalDateTime.now()
        val inventoryId = inventory.id ?: BigInteger.ZERO
        // Add inventory record for revert
        val inventoryRecord = InventoryRecord(
            inventoryId = inventoryId,
            recordType = "INVENTORY_REVERTED",
            inventoryCount = inventory.inventoryCount * -1,
            createdAt = now,
        )
        inventoryRecordRepository.save(inventoryRecord)
        // Update inventory
        inventory.inventoryCount = 0
        inventory.status = "CANCELED"
        inventory.updatedAt = now
        inventoryRepository.save(inventory)
    }
}