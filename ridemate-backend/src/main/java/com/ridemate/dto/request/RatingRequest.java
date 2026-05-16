package com.ridemate.dto.request;

import com.ridemate.model.enums.RatingParameter;
import lombok.Data;
import java.util.Map;

@Data
public class RatingRequest {
    private Long bookingId;
    private Long rateeId;
    private Map<RatingParameter, Integer> ratings; // parameter -> stars (1-5)
}