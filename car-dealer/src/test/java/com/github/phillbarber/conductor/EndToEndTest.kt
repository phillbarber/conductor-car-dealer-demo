package com.github.phillbarber.conductor

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
class EndToEndTest {


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