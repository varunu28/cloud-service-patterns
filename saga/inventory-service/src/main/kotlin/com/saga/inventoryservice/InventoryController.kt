package com.saga.inventoryservice

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.math.BigInteger

@RestController
@RequestMapping("/api/v1/inventory")
class InventoryController(val inventoryService: InventoryService) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun reserveInventory(@RequestBody request: ReserveInventoryDto) {
        inventoryService.persistInventory(
            customerName = request.customerName,
            inventoryCount = request.inventoryCount,
            orderId = BigInteger(request.orderId),
        )
    }

    @PostMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    fun revertInventoryReservation(@PathVariable("orderId") orderId: BigInteger) {
        inventoryService.revertInventory(orderId)
    }
}