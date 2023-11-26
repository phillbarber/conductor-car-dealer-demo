package com.github.phillbarber.conductor.remoteservices;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class OrderRemoteService{
    private HttpClient httpClient;
    private String rootURI;

    public OrderRemoteService(HttpClient httpClient, String rootURI) {
        this.httpClient = httpClient;
        this.rootURI = rootURI;
    }

    public boolean isOrderValid() throws IOException {
        ClassicHttpResponse execute = httpClient.execute(new HttpPost(rootURI + "/api/v1/checkOrder"),
                response -> {
                    String bodyAsString = EntityUtils.toString(response.getEntity());

                    return response;
                });


    }

}
