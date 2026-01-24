package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AppSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login.html", "/favicon.ico",
                    "/logoHotel.jpeg",
                    "/script.js",
                    "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.svg"
                ).permitAll()
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html", true)
                .failureUrl("/login.html?error=1")
                .permitAll()
            )

            .rememberMe(rm -> rm
                .key("troque-essa-chave-por-uma-bem-grande-e-secreta")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(60 * 60 * 24 * 30)
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=1")
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ cria usuário admin a partir das variáveis do Railway
    @Bean
    public UserDetailsService userDetailsService(Environment env, PasswordEncoder encoder) {
        String user = env.getProperty("ADMIN_USER", "admin");
        String pass = env.getProperty("ADMIN_PASS", "admin123");

        UserDetails admin = User.withUsername(user)
                .password(encoder.encode(pass))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}