package com.dmdev.jdbc.starter.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Flight (Integer id,
        String flightNo,
        LocalDateTime departureDate,
        String departureAirportCode,
        LocalDateTime arrivalDate,
        String arrivalAirportCode,
        Integer aircraftId,
        String status) {
        }