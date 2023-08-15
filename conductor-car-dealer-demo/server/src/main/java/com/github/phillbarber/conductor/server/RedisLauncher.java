package com.github.phillbarber.conductor.server;

import redis.embedded.RedisServer;

import java.io.IOException;

public class RedisLauncher {

    public static void main(String[] args) throws IOException {
        redis.embedded.RedisServer redisServer = new RedisServer(6379);
        redisServer.start();
// do some work

    }
}
