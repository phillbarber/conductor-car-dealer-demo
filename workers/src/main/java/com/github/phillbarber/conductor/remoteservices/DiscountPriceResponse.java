package com.github.phillbarber.conductor.remoteservices;

import java.math.BigDecimal;

public record DiscountPriceResponse (BigDecimal discount, String promotionCode, Integer totalPrice) {
}
