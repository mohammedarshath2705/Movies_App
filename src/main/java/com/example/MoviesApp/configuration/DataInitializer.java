package com.example.MoviesApp.configuration;

import com.example.MoviesApp.entity.AuthProvider;
import com.example.MoviesApp.entity.Role;
import com.example.MoviesApp.entity.User;
import com.example.MoviesApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.fullname}")
    private String fullName;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .fullName(fullName)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .provider(AuthProvider.LOCAL)
                    .emailVerified(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: " + adminEmail);
        } else {
            System.out.println("️Admin user already exists, skipping creation.");
        }
    }
}
