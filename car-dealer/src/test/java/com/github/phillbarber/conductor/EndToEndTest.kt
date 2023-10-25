package com.github.phillbarber.conductor

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
class EndToEndTest {


    /*
    Idea for workflow

    1. Check Order is Valid - checkOrder - returns validBoolean and message
    2. Get Customer Details - getCustomer - returns customerName and loyaltyPoints
    3. Get Base Price       - getBasePrice - returns price for car and customer
    4. Get Extra Price      - getPriceForExtras - takes car and extras
    5. Discount Service     - getDiscount - returns an amount less
    6. Save Order           - saveOrder   - saves order to DB
    Returns Valid order with price
     */

    @Container
    var redis = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379)

    @Test
    fun stuff(){
        assertTrue(true)
        val address: String = redis.host
        val port: Int = redis.firstMappedPort

        System.out.println("NICE")
        System.out.println("${redis.host} ")
        System.out.println(" ${redis.firstMappedPort} ")
        System.out.println("YAY")
        Thread.sleep(10000)


    }
}