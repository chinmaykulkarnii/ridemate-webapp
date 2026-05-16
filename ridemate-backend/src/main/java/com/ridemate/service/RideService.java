package com.ridemate.service;

import com.ridemate.dto.request.RideRequest;
import com.ridemate.model.Ride;
import com.ridemate.model.User;
import com.ridemate.model.enums.VehicleType;
import com.ridemate.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserService userService;

    public Ride createRide(Long driverId, RideRequest request) {
        User driver = userService.getUserById(driverId);

        Ride ride = Ride.builder()
                .driver(driver)
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .departureTime(request.getDepartureTime())
                .vehicleType(request.getVehicleType())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .pricePerSeat(request.getPricePerSeat())
                .vehicleModel(request.getVehicleModel())
                .vehicleNumber(request.getVehicleNumber())
                .additionalInfo(request.getAdditionalInfo())
                .build();

        return rideRepository.save(ride);
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
    }

    public List<Ride> getRidesByDriver(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> searchRides(String origin, String destination,
                                  LocalDateTime departureTime, Integer seatsRequired) {
        return rideRepository.searchRides(origin, destination, departureTime, seatsRequired);
    }

    public List<Ride> searchRidesWithFilters(String origin, String destination,
                                             LocalDateTime departureTime, Integer seatsRequired,
                                             VehicleType vehicleType, Double maxPrice) {
        return rideRepository.searchRidesWithFilters(origin, destination,
                departureTime, seatsRequired, vehicleType, maxPrice);
    }

    public Ride updateRide(Long id, RideRequest request) {
        Ride ride = getRideById(id);

        ride.setOrigin(request.getOrigin());
        ride.setDestination(request.getDestination());
        ride.setDepartureTime(request.getDepartureTime());
        ride.setVehicleType(request.getVehicleType());
        ride.setPricePerSeat(request.getPricePerSeat());
        ride.setVehicleModel(request.getVehicleModel());
        ride.setVehicleNumber(request.getVehicleNumber());
        ride.setAdditionalInfo(request.getAdditionalInfo());

        return rideRepository.save(ride);
    }

    public void deleteRide(Long id) {
        Ride ride = getRideById(id);
        ride.setIsActive(false);
        rideRepository.save(ride);
    }

    public void updateAvailableSeats(Long rideId, Integer seatsChange) {
        Ride ride = getRideById(rideId);
        ride.setAvailableSeats(ride.getAvailableSeats() + seatsChange);
        rideRepository.save(ride);
    }
}