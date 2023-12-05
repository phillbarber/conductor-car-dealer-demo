package com.github.phillbarber.conductor.remoteservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.OrderRequest;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class BasePriceRemoteService {
    private HttpClient httpClient;
    private final String serviceRootURI;
    private ObjectMapper objectMapper;

    public BasePriceRemoteService(HttpClient httpClient, String serviceRootURI, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.serviceRootURI = serviceRootURI;
        this.objectMapper = objectMapper;
    }

    public BasePriceResponse getBasePrice(OrderRequest orderRequest) {
        HttpPost httpPost = new HttpPost( serviceRootURI + "/price-service/api/v1/price");
        try {
            httpPost.setEntity(new StringEntity(objectMapper.writer().writeValueAsString(orderRequest)));
            String execute = httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
            return objectMapper.reader().readValue(execute, BasePriceResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


