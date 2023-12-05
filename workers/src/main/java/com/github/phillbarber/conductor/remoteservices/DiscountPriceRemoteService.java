package com.github.phillbarber.conductor.remoteservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.OrderRequest;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.util.HashMap;

public class DiscountPriceRemoteService {
    private HttpClient httpClient;
    private final String serviceRootURI;
    private ObjectMapper objectMapper;

    public DiscountPriceRemoteService(HttpClient httpClient, String serviceRootURI, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.serviceRootURI = serviceRootURI;
        this.objectMapper = objectMapper;
    }

    public DiscountPriceResponse getDiscountPrice(OrderRequest orderRequest, Integer basePrice, Integer customerLoyaltyPoints) {
        try {

            HttpPost request = new HttpPost( serviceRootURI + "/discount-service/api/v1/price");
            request.setEntity(new StringEntity(objectMapper.writer().writeValueAsString(buildRequest(orderRequest, basePrice, customerLoyaltyPoints))));

            String execute = httpClient.execute(request, new BasicHttpClientResponseHandler());
            return objectMapper.reader().readValue(execute, DiscountPriceResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<Object, Object> buildRequest(OrderRequest orderRequest, Integer basePrice, Integer customerLoyaltyPoints) {
        HashMap<Object, Object> request = new HashMap<>();
        request.put("order", orderRequest);
        request.put("basePrice", basePrice);
        request.put("customerLoyaltyPoints", customerLoyaltyPoints);
        return request;
    }
}


