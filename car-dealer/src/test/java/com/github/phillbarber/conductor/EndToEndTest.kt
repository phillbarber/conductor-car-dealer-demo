package com.github.phillbarber.conductor

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
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

    input:

    order
     car
      make
      model
      extras

    customer
     id


     returns:

      order
       id
       price
        basePrice
        extraPrice
        saving
       car
        make
        model
        extras



     */

    var network = Network.newNetwork()

    @Container
    var redis = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379)
        .withNetwork(network)
        .withNetworkAliases("rs")

    @Container
    var elasticSearch = GenericContainer(DockerImageName.parse("elasticsearch:6.8.15"))
        .withEnv("ransport.host", "0.0.0.0")
        .withEnv("discovery.type", "single-node")
        .withEnv("xpack.security.enabled", "false")
        .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx1024m")
        .withExposedPorts(9200, 9300)
        .withNetwork(network)
        .withNetworkAliases("es")


//    @Container
//    var conductorServer = GenericContainer(DockerImageName.parse("conductor:server")).withExposedPorts(8080).withNetwork(network)




    @Test
    fun stuff(){
        assertTrue(true)
        val address: String = redis.host
        val port: Int = redis.firstMappedPort

        System.out.println("NICE")
        System.out.println("${redis.host} ")
        System.out.println("${elasticSearch.host} ")
        System.out.println(" ${redis.firstMappedPort} ")
        System.out.println(" ${elasticSearch.firstMappedPort} ")
        System.out.println(" ${redis.firstMappedPort} ")
        System.out.println("YAY")
        Thread.sleep(10000)


    }
}
