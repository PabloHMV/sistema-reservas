package com.example.demo.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserSeeder(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        // Pega do Railway (recomendado)
        String adminUser = System.getenv().getOrDefault("ADMIN_USER", "admin");
        String adminPass = System.getenv().getOrDefault("ADMIN_PASS", "admin123");

        if (repo.findByUsername(adminUser).isEmpty()) {
            repo.save(new AppUser(adminUser, encoder.encode(adminPass), "ADMIN"));
            System.out.println("✅ Admin criado: " + adminUser + " (troque ADMIN_PASS no Railway!)");
        }
    }
}
