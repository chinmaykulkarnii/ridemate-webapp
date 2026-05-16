package com.ridemate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class is used as a response after successful login/signup.
 * It returns the JWT token along with basic user details to the frontend.
 */
@Data // Generates getters, setters, toString, equals, hashCode automatically
@AllArgsConstructor // Generates a constructor with all fields as parameters
public class JwtResponse {

    // JWT token that the client will store (e.g., in localStorage) and send in Authorization header
    private String token;

    // Token type, usually "Bearer", indicates how the token should be used in the Authorization header
    private String type = "Bearer";

    // User's unique ID from the database
    private Long id;

    // Email of the authenticated user
    private String email;

    // User's first name
    private String firstName;

    // User's last name
    private String lastName;

    // No need for manual constructor - @AllArgsConstructor already generates:
    // public JwtResponse(String token, String type, Long id, String email,
    //                    String firstName, String lastName)
}