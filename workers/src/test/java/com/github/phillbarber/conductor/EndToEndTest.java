package com.github.phillbarber.conductor;

import com.github.phillbarber.conductor.facade.FacadeLanucher;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
public class EndToEndTest {

    private Network network = Network.newNetwork();

    @Container
    private GenericContainer redis = getRedisContainer();

    @Container
    private GenericContainer elastic = getElasticSearchContainer();

    @Container
    private GenericContainer conductorServer = getConductorContainer();


    @Test
    public void stuff() throws InterruptedException {
        FacadeLanucher.main(null);
        assertTrue(redis.isRunning());
        assertTrue(conductorServer.isRunning());
        assertTrue(elastic.isRunning());


    }

    private GenericContainer getRedisContainer() {
        return new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
                .withExposedPorts(6379)
                .withNetwork(network)
                .withNetworkAliases("rs");
    }


    private GenericContainer getElasticSearchContainer() {
        return new GenericContainer(DockerImageName.parse("elasticsearch:6.8.15"))
                .withEnv("transport.host", "0.0.0.0")
                .withEnv("discovery.type", "single-node")
                .withEnv("xpack.security.enabled", "false")
                .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx1024m")
                .withExposedPorts(9200, 9300)
                .withNetwork(network)
                .withNetworkAliases("es");
    }

    //Not specifying the config properties file

    private GenericContainer getConductorContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:server"))
                .withEnv("CONFIG_PROP", "config-local.properties")//this corresponds to https://github.com/Netflix/conductor/blob/f013a53b345b21e890790c8b7a316a34d992fc2e/docker/server/config/config-local.properties
                .withExposedPorts(8080)
                .withNetwork(network);
    }


}
