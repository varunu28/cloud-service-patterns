package com.saga.inventoryservice

import com.saga.inventoryservice.kafka.InventoryEvent
import com.saga.inventoryservice.kafka.KafkaTopics.INVENTORY_RESERVATION_FAILED_TOPIC
import com.saga.inventoryservice.kafka.KafkaTopics.INVENTORY_RESERVATION_SUCCESSFUL_TOPIC
import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository,
    private val inventoryRecordRepository: InventoryRecordRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    @Value("\${inventory.count.threshold}")
    private var inventoryCountThreshold: Int = 1000

    @Transactional
    fun persistInventory(
        customerName: String,
        inventoryCount: Int,
        orderId: BigInteger,
    ) {
        if (inventoryCount > inventoryCountThreshold) {
            val inventoryFailedEvent = InventoryEvent(orderId = orderId.toString())
            val eventString = Json.encodeToString(inventoryFailedEvent)
            kafkaTemplate.send(INVENTORY_RESERVATION_FAILED_TOPIC, eventString)
            return
        }
        val now = LocalDateTime.now()
        val inventory = Inventory(
            customerName = customerName,
            orderId = orderId,
            inventoryCount = inventoryCount,
            createdAt = now,
            updatedAt = now,
            status = "CREATED"
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
        val inventorySuccessEvent = InventoryEvent(orderId = orderId.toString())
        val eventString = Json.encodeToString(inventorySuccessEvent)
        kafkaTemplate.send(INVENTORY_RESERVATION_SUCCESSFUL_TOPIC, eventString)
    }
}