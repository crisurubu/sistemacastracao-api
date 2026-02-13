package com.projetoong.sistema_castracao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 1. PRIORIDADE ZERO: Liberação de Pre-flight (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. ROTAS PÚBLICAS (Acesso sem Token)
                        // Colocamos o /api/publico/** no topo para o Spring não exigir JWT aqui
                        // Exemplo no SecurityConfig.java
                        .requestMatchers("/api/cadastros/**").permitAll()
                        .requestMatchers("/api/public/**", "/api/publico/**", "/api/tutores/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").permitAll()

                        // 3. ÁREA DA CLÍNICA
                        .requestMatchers("/api/clinica/**").hasAnyAuthority("CLINICA", "ROLE_CLINICA", "MASTER", "ROLE_MASTER")

                        // 4. OPERACIONAL ONG (Alertas, Tutores e Agendamentos)
                        .requestMatchers("/api/admin/alarmes/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "ROLE_MASTER", "ROLE_VOLUNTARIO")
                        .requestMatchers("/api/admin/tutores/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "ROLE_MASTER", "ROLE_VOLUNTARIO")
                        .requestMatchers("/api/admin/agendamentos/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "CLINICA", "ROLE_MASTER", "ROLE_VOLUNTARIO", "ROLE_CLINICA")

                        // 5. GESTÃO MASTER
                        .requestMatchers("/api/admin/clinicas/**").hasAnyAuthority("MASTER", "ROLE_MASTER")

                        // QUALQUER OUTRA ROTA EXIGE TOKEN
                        .anyRequest().authenticated()
                )
                // O filtro de segurança deve vir DEPOIS das definições de permitAll para evitar 403 em rotas abertas
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}