
package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Seu front é estático e consome API com fetch
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // libera login e arquivos estáticos
                .requestMatchers(
                    "/login.html", "/favicon.ico",
                    "/logoHotel.jpeg",
                    "/script.js",
                    "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.svg"
                ).permitAll()

                // libera a página de login como "home" se quiser
                .requestMatchers("/").permitAll()

                // TODO o resto exige login (inclusive index.html/lista.html/editar.html e /reservas)
                .anyRequest().authenticated()
            )

            // Form login padrão do Spring, usando sua página customizada
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")          // endpoint que recebe user/pass
                .defaultSuccessUrl("/index.html", true)
                .failureUrl("/login.html?error=1")
                .permitAll()
            )

            .rememberMe(rm -> rm
                .key("troque-essa-chave-por-uma-bem-grande-e-secreta")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(60 * 60 * 24 * 30) // 30 dias
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=1")
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )

            .httpBasic(Customizer.withDefaults()); // opcional (útil p/ testes)

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
