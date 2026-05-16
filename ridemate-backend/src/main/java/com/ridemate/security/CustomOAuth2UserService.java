package com.ridemate.security;

import com.ridemate.model.User;
import com.ridemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getAttribute("sub"); // Google's user ID
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String picture = oauth2User.getAttribute("picture");

        // Create final variables for lambda expression
        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        final String finalProviderId = providerId;
        final String finalPicture = picture;
        final String finalProvider = provider;

        // Check if user already exists
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Create new user
                    User newUser = User.builder()
                            .email(email)
                            .firstName(finalFirstName != null ? finalFirstName : "User")
                            .lastName(finalLastName != null ? finalLastName : "")
                            .provider(finalProvider.toUpperCase())
                            .providerId(finalProviderId)
                            .profilePicture(finalPicture)
                            .build();
                    return userRepository.save(newUser);
                });

        // Update provider info if user exists but was created locally
        if (user.getProvider() == null) {
            user.setProvider(provider.toUpperCase());
            user.setProviderId(providerId);
            if (picture != null && user.getProfilePicture() == null) {
                user.setProfilePicture(picture);
            }
            userRepository.save(user);
        }

        return oauth2User;
    }
}
