package com.github.phillbarber.conductor.remoteservices;

import java.math.BigDecimal;

public record BasePriceResponse (BigDecimal basePrice, String currency){
}
