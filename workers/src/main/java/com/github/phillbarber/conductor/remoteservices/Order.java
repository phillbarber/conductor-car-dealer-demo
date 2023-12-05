package com.github.phillbarber.conductor.remoteservices;

import java.math.BigDecimal;

public record Order(
        String id,
        String customerId,
        String customerName,
        Integer customerLoyaltyPoints,
        BigDecimal basePrice,
        String currency,
        BigDecimal totalPrice,
        String promotionCode,
        BigDecimal discount
) {

    public Order withId(String id){
        //no .copy() method like there is in Kotlin :(
        return new Order(id, customerId, customerName, customerLoyaltyPoints, basePrice, currency, totalPrice, promotionCode, discount);
    }
}
