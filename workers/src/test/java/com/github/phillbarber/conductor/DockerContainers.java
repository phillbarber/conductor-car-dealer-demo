package com.github.phillbarber.conductor;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class DockerContainers {


    private Network network = Network.newNetwork();


    public GenericContainer getRedisContainer() {
        return new GenericContainer(DockerImageName.parse("redis:6.2.3-alpine"))
                .withExposedPorts(6379)
                .withNetwork(network)
                .withNetworkAliases("rs");
    }


    public GenericContainer getConductorUIContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:ui"))
                .withEnv("WF_SERVER", "http://conductor-server:8080")
                .withExposedPorts(5000, 5000)
                .withNetwork(network);
    }

    public GenericContainer getElasticSearchContainer() {
        return new GenericContainer(DockerImageName.parse("elasticsearch:6.8.15"))
                .withEnv("transport.host", "0.0.0.0")
                .withEnv("discovery.type", "single-node")
                .withEnv("xpack.security.enabled", "false")
                .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx1024m")
                .withExposedPorts(9200, 9300)
                .withNetwork(network)
                .withNetworkAliases("es");
    }

    public GenericContainer getConductorContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:server"))
                .withEnv("CONFIG_PROP", "config-local.properties")//this corresponds to https://github.com/Netflix/conductor/blob/f013a53b345b21e890790c8b7a316a34d992fc2e/docker/server/config/config-local.properties
                .withExposedPorts(8080)
                .withNetworkAliases("conductor-server")
                .withNetwork(network);
    }


}
