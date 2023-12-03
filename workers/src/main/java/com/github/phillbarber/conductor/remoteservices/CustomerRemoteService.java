package com.github.phillbarber.conductor.remoteservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.Order;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class CustomerRemoteService {
    private HttpClient httpClient;
    private String rootURI;
    private ObjectMapper objectMapper;

    public CustomerRemoteService(HttpClient httpClient, String rootURI, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.rootURI = rootURI;
        this.objectMapper = objectMapper;
    }

    public CustomerResponse getCustomer(String customerId) {
        try {
            String uri = rootURI + "/customer-service/api/v1/customer/" + customerId;
            String execute = httpClient.execute(new HttpGet(uri), new BasicHttpClientResponseHandler());
            return objectMapper.reader().readValue(execute, CustomerResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


