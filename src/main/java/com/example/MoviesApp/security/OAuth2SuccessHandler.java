package com.example.MoviesApp.security;

import com.example.MoviesApp.entity.AuthProvider;
import com.example.MoviesApp.entity.Role;
import com.example.MoviesApp.entity.User;
import com.example.MoviesApp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        String mode = request.getParameter("mode");
        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        User user;

        if (existingUserOpt.isEmpty()) {

            if ("signup".equalsIgnoreCase(mode)) {
                user = User.builder()
                        .email(email)
                        .fullName(name)
                        .username(generateUsernameFromEmail(email))
                        .provider(AuthProvider.GOOGLE)
                        .emailVerified(true)
                        .role(Role.USER)
                        .build();

                userRepository.save(user);

            } else {
                response.sendRedirect(frontendUrl + "/login?error=Google account not registered. Please sign up first.");
                return;
            }

        } else {
            user = existingUserOpt.get();
        }

        String token = jwtUtil.generateToken(user);

        String redirectUrl =
                frontendUrl + "/oauth-success"
                        + "?token=" + token
                        + "&userId=" + user.getId()
                        + "&username=" + user.getUsername()
                        + "&email=" + email;

        response.sendRedirect(redirectUrl);
    }

    private String generateUsernameFromEmail(String email) {
        return email.split("@")[0] + "_" + UUID.randomUUID().toString().substring(0, 5);
    }
}

