package com.ridemate.dto.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String referralCode;  // Optional referral code
}