package com.ridemate.dto.request;

import com.ridemate.model.enums.VehicleType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RideRequest {
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private VehicleType vehicleType;
    private Integer totalSeats;
    private Double pricePerSeat;
    private String vehicleModel;
    private String vehicleNumber;
    private String additionalInfo;
}