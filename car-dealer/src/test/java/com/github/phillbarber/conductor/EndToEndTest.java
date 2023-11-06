package com.github.phillbarber.conductor;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Testcontainers
public class EndToEndTest {


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

    private Network network = Network.newNetwork();


    @Container
    private GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379)
        .withNetwork(network)
        .withNetworkAliases("rs");


    @Container
    private GenericContainer elastic = new GenericContainer(DockerImageName.parse("elasticsearch:6.8.15"))
        .withEnv("transport.host", "0.0.0.0")
        .withEnv("discovery.type", "single-node")
        .withEnv("xpack.security.enabled", "false")
        .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx1024m")
        .withExposedPorts(9200, 9300)
        .withNetwork(network)
        .withNetworkAliases("es");


    //Not specifying the config properties file
    @Container //dont forget .withExposedPorts(8080)
    private GenericContainer conductorServer = new GenericContainer(DockerImageName.parse("conductor:server"))
        .withEnv("CONFIG_PROP", "config-local.properties")//this corresponds to https://github.com/Netflix/conductor/blob/f013a53b345b21e890790c8b7a316a34d992fc2e/docker/server/config/config-local.properties
        .withExposedPorts(8080)
        .withNetwork(network);


    @Test
    public void stuff() throws InterruptedException {


        Thread.sleep(100000);



    }


}
