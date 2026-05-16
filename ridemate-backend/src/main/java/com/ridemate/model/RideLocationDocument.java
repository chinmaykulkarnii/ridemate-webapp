package com.ridemate.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "ride_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideLocationDocument {
    @Id
    private String id;

    private Long rideId;
    private Long driverId;

    private Double latitude;
    private Double longitude;

    private Double speed;  // in km/h
    private Double heading;  // direction in degrees

    private Integer estimatedTimeToPickup;  // in minutes
    private Double distanceToPickup;  // in km

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
