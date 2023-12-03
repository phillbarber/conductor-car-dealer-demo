package com.github.phillbarber.conductor.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
public class StubServices {

    public static final String OrderServiceCheckOrder = "/order-service/api/v1/checkOrder";
    public static final String CustomerServiceRoot = "/customer-service/api/v1/customer/";
    public static final String PriceService = "/price-service/api/v1/price/";
    public static final String DiscountService = "/discount-service/api/v1/price/";
    public static final String OrderServiceSaveOrder = "/order-service/api/v1/order/";

    public void orderServiceReturnsInvalidOrderFor(String carMake) {
        stubFor(post(OrderServiceCheckOrder).withRequestBody(containing(carMake)).willReturn(ok().withBody("""
                {
                     "rejectionMessage" : "Sorry we don't sell Sentinels",
                     "isValid": false
                 }
                """)));
    }

    public void orderServiceReturnsValidOrderFor(String carMake) {
        stubFor(post(OrderServiceCheckOrder).withRequestBody(containing(carMake)).willReturn(ok().withBody("""
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
                     "basePrice" : 60000,
                     "currency" : "GBP"
                 }
                """)));
    }

    public void discountServiceReturns() {
        stubFor(post(urlPathMatching(DiscountService)).willReturn(ok().withBody("""
                {
                     "discount" : 0.1,
                     "promotionCode" : "ABCDE1234"
                 }
                """)));
    }

    public void saveOrderReturnsOK() {
        stubFor(post(urlPathMatching(OrderServiceSaveOrder)).willReturn(ok().withBody("""
                {
                  "id" : "123456",
                  "customerId" : "1234",
                  "car" : {
                      "make": "Blista",
                      "model": "someModel"
                    },
                  "basePrice" : 60000,
                  "totalPrice" : 54000,
                  "currency" : "GBP",
                  "promotionCode" : "ABCDE1234",
                  "discount" : 0.1
                 }
                """)));
    }

}
