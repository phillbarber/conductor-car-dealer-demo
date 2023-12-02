package com.github.phillbarber.conductor.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
public class OrderServiceStub {

    public void prepareInvalidOrder(String carMake) {
        stubFor(post("/order-service/api/v1/checkOrder").withRequestBody(containing(carMake)).willReturn(ok().withBody("""
                {
                     "rejectionMessage" : "Sorry we don't sell Sentinels",
                     "isValid": false
                 }
                """)));
    }

    public void prepareValidOrder(String carMake) {
        stubFor(post("/order-service/api/v1/checkOrder").withRequestBody(containing(carMake)).willReturn(ok().withBody("""
                {
                     "rejectionMessage" : null,
                     "isValid": true
                 }
                """)));
    }

}
