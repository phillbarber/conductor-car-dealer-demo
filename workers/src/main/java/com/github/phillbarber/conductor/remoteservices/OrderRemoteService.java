package com.github.phillbarber.conductor.remoteservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.Order;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class OrderRemoteService {
    private HttpClient httpClient;
    private String rootURI;

    public OrderRemoteService(HttpClient httpClient, String rootURI) {
        this.httpClient = httpClient;
        this.rootURI = rootURI;
    }

    public OrderValidationResponse getValidationResponse(Order order) {
        HttpPost httpPost = new HttpPost(rootURI + "/api/v1/checkOrder");
        try {
            httpPost.setEntity(new StringEntity(new ObjectMapper().writer().writeValueAsString(order)));
            String execute = httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
            return new ObjectMapper().reader().readValue(execute, OrderValidationResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


