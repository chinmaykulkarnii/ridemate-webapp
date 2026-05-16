package com.ridemate.service;

import com.ridemate.dto.request.LoginRequest;
import com.ridemate.dto.request.SignupRequest;
import com.ridemate.dto.response.JwtResponse;
import com.ridemate.model.User;
import com.ridemate.model.UserRole;
import com.ridemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GamificationService gamificationService;

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (user.getPassword() == null) {
            throw new RuntimeException("This account uses OAuth. Please login with " + user.getProvider());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public User signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Assign default roles: both RIDE_PUBLISHER and RIDE_CONSUMER
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.RIDE_PUBLISHER);
        roles.add(UserRole.RIDE_CONSUMER);

        // Generate unique referral code
        String referralCode = "REF" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .provider("LOCAL")
                .roles(roles)
                .referralCode(referralCode)
                .build();

        user = userRepository.save(user);

        // Apply referral code if provided
        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            gamificationService.applyReferralCode(user, request.getReferralCode());
        }

        return user;
    }
}