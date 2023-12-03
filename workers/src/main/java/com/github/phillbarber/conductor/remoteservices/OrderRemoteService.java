package com.github.phillbarber.conductor.remoteservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.Order;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class OrderRemoteService {
    private HttpClient httpClient;
    private String rootURI;
    private ObjectMapper objectMapper;

    public OrderRemoteService(HttpClient httpClient, String rootURI, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.rootURI = rootURI + "/order-service/api/v1/";
        this.objectMapper = objectMapper;
    }

    public OrderValidationResponse getValidationResponse(Order order) {
        HttpPost httpPost = new HttpPost(rootURI + "checkOrder");
        try {
            httpPost.setEntity(new StringEntity(objectMapper.writer().writeValueAsString(order)));
            String execute = httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
            return objectMapper.reader().readValue(execute, OrderValidationResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


