package com.ridemate.dto.request;

import lombok.Data;

@Data
public class BookingRequest {
    private Long rideId;
    private Integer seatsBooked;
}