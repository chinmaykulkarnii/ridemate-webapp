package com.ridemate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LocationService {

    private final RestTemplate restTemplate;
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org";
    private static final String OSRM_BASE_URL = "https://router.project-osrm.org";

    public LocationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Geocode an address to coordinates using Nominatim (OpenStreetMap)
     */
    public GeocodeResult geocode(String address) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_BASE_URL + "/search")
                    .queryParam("q", address)
                    .queryParam("format", "json")
                    .queryParam("limit", "1")
                    .toUriString();

            List<Map<String, Object>> results = restTemplate.getForObject(url, List.class);

            if (results != null && !results.isEmpty()) {
                Map<String, Object> result = results.get(0);
                return new GeocodeResult(
                        Double.parseDouble(result.get("lat").toString()),
                        Double.parseDouble(result.get("lon").toString()),
                        result.get("display_name").toString()
                );
            }
        } catch (Exception e) {
            log.error("Error geocoding address: {}", address, e);
        }
        return null;
    }

    /**
     * Reverse geocode coordinates to address using Nominatim
     */
    public String reverseGeocode(Double lat, Double lng) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_BASE_URL + "/reverse")
                    .queryParam("lat", lat)
                    .queryParam("lon", lng)
                    .queryParam("format", "json")
                    .toUriString();

            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("display_name")) {
                return result.get("display_name").toString();
            }
        } catch (Exception e) {
            log.error("Error reverse geocoding: {}, {}", lat, lng, e);
        }
        return null;
    }

    /**
     * Get route information (distance and duration) using OSRM
     */
    public RouteInfo getRouteInfo(Double startLat, Double startLng, Double endLat, Double endLng) {
        try {
            String coordinates = String.format("%f,%f;%f,%f", startLng, startLat, endLng, endLat);
            String url = UriComponentsBuilder.fromHttpUrl(OSRM_BASE_URL + "/route/v1/driving/" + coordinates)
                    .queryParam("overview", "false")
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "Ok".equals(response.get("code"))) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                if (routes != null && !routes.isEmpty()) {
                    Map<String, Object> route = routes.get(0);
                    Double distance = ((Number) route.get("distance")).doubleValue() / 1000; // Convert to km
                    Integer duration = ((Number) route.get("duration")).intValue() / 60; // Convert to minutes

                    return new RouteInfo(distance, duration);
                }
            }
        } catch (Exception e) {
            log.error("Error getting route info", e);
            // Fallback to Haversine formula
            return getRouteInfoFallback(startLat, startLng, endLat, endLng);
        }
        return null;
    }

    /**
     * Fallback route calculation using Haversine formula
     */
    private RouteInfo getRouteInfoFallback(Double startLat, Double startLng, Double endLat, Double endLng) {
        double distance = calculateHaversineDistance(startLat, startLng, endLat, endLng);
        int duration = (int) (distance / 50 * 60); // Assume average 50 km/h, convert to minutes
        return new RouteInfo(distance, duration);
    }

    /**
     * Calculate distance between two points using Haversine formula
     */
    public double calculateHaversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Radius of the Earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Search for places/addresses using Nominatim autocomplete
     */
    public List<PlaceSuggestion> searchPlaces(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_BASE_URL + "/search")
                    .queryParam("q", query)
                    .queryParam("format", "json")
                    .queryParam("limit", "10")
                    .queryParam("addressdetails", "1")
                    .toUriString();

            List<Map<String, Object>> results = restTemplate.getForObject(url, List.class);

            if (results != null) {
                return results.stream()
                        .map(result -> new PlaceSuggestion(
                                result.get("display_name").toString(),
                                Double.parseDouble(result.get("lat").toString()),
                                Double.parseDouble(result.get("lon").toString()),
                                result.get("type") != null ? result.get("type").toString() : "place"
                        ))
                        .toList();
            }
        } catch (Exception e) {
            log.error("Error searching places: {}", query, e);
        }
        return List.of();
    }

    // Inner classes for return types
    public record GeocodeResult(Double latitude, Double longitude, String displayName) {}
    public record RouteInfo(Double distance, Integer duration) {}
    public record PlaceSuggestion(String displayName, Double latitude, Double longitude, String type) {}
}
