package com.saga.orderservice.saga

import dev.dbos.transact.workflow.Step
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.math.BigInteger

@Service
class OrderStepService(
    @Qualifier("paymentServiceRestClient") private val paymentServiceClient: RestClient,
    @Qualifier("inventoryServiceRestClient") private val inventoryServiceRestClient: RestClient
) {

    @Step
    fun chargePayment(
        orderId: BigInteger,
        amount: Double,
        idempotencyKey: String,
        customerName: String
    ): Boolean {
        val request = CreatePaymentDto(
            orderId = orderId.toString(),
            amount = amount,
            idempotencyKey = idempotencyKey,
            customerName = customerName
        )
        val response = paymentServiceClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity()
        return response.statusCode.is2xxSuccessful
    }

    @Step
    fun reserveInventory(
        orderId: BigInteger,
        inventoryCount: Int,
        customerName: String
    ): Boolean {
        val request = ReserveInventoryDto(
            orderId = orderId.toString(),
            inventoryCount = inventoryCount,
            customerName = customerName
        )
        val response = inventoryServiceRestClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity()
        return response.statusCode.is2xxSuccessful
    }

    @Step
    fun reversePayment(orderId: BigInteger) {
        paymentServiceClient.post()
            .uri("/$orderId")
            .retrieve()
            .toBodilessEntity()
    }

    @Step
    fun revertInventoryReservation(orderId: BigInteger) {
        inventoryServiceRestClient.post()
            .uri("/$orderId")
            .retrieve()
            .toBodilessEntity()
    }
}