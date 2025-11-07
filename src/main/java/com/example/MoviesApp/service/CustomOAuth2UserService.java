package com.example.MoviesApp.service;

import com.example.MoviesApp.entity.AuthProvider;
import com.example.MoviesApp.entity.Role;
import com.example.MoviesApp.entity.User;
import com.example.MoviesApp.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            User newUser = User.builder()
                    .email(email)
                    .username(name)
                    .fullName(name)
                    .provider(AuthProvider.GOOGLE)
                    .emailVerified(true)
                    .role(Role.USER)
                    .build();
            userRepository.save(newUser);
        }

        return oAuth2User;
    }
}
