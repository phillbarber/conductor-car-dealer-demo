package com.github.phillbarber.conductor.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
public class StubServices {

    public static final String OrderServiceRoot = "/order-service/api/v1/checkOrder";
    public static final String CustomerServiceRoot = "/customer-service/api/v1/customer/";
    public static final String PriceService = "/price-service/api/v1/price/";

    public void orderServiceReturnsInvalidOrderFor(String carMake) {
        stubFor(post(OrderServiceRoot).withRequestBody(containing(carMake)).willReturn(ok().withBody("""
                {
                     "rejectionMessage" : "Sorry we don't sell Sentinels",
                     "isValid": false
                 }
                """)));
    }

    public void orderServiceReturnsValidOrderFor(String carMake) {
        stubFor(post(OrderServiceRoot).withRequestBody(containing(carMake)).willReturn(ok().withBody("""
                {
                     "rejectionMessage" : null,
                     "isValid": true
                 }
                """)));
    }

    public void customerServiceReturnsCustomerFor(String customerId) {
        stubFor(get(urlPathMatching(CustomerServiceRoot + customerId)).willReturn(ok().withBody("""
                {
                     "name" : "Marty McFly",
                     "loyaltyPoints": 12
                 }
                """)));
    }

    public void customerServiceReturnsNotFoundCustomerFor(String customerId) {
        stubFor(get(urlPathMatching(CustomerServiceRoot + customerId)).willReturn(notFound()));
    }

    public void priceServiceReturnsPrice() {
        stubFor(post(urlPathMatching(PriceService)).willReturn(ok().withBody("""
                {
                     "basePriceInPounds" : 60000,
                 }
                """)));
    }

}
