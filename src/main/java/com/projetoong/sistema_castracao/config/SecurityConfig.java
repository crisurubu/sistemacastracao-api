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

                        // 2. EXCEÇÕES PÚBLICAS ESPECÍFICAS (Para evitar 403 no Cadastro)
                        // Liberamos apenas a LEITURA da chave ativa para o Tutor conseguir pagar
                        .requestMatchers(HttpMethod.GET, "/api/admin/configuracao-pix/ativa").permitAll()

                        // 3. ROTAS PÚBLICAS GERAIS (Acesso sem Token)
                        .requestMatchers(HttpMethod.GET, "/api/sistema/status").permitAll()
                        .requestMatchers("/api/cadastros/**").permitAll()
                        .requestMatchers("/api/public/**", "/api/publico/**", "/api/tutores/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").permitAll()

                        // 4. ÁREA DA CLÍNICA
                        .requestMatchers("/api/clinica/**").hasAnyAuthority("CLINICA", "ROLE_CLINICA", "MASTER", "ROLE_MASTER")

                        // 5. OPERACIONAL ONG (Alertas, Tutores, Agendamentos e Extrato)
                        .requestMatchers("/api/admin/alarmes/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "ROLE_MASTER", "ROLE_VOLUNTARIO")
                        .requestMatchers("/api/admin/tutores/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "ROLE_MASTER", "ROLE_VOLUNTARIO")
                        .requestMatchers("/api/admin/agendamentos/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "CLINICA", "ROLE_MASTER", "ROLE_VOLUNTARIO", "ROLE_CLINICA")

                        // Extrato de Auditoria
                        .requestMatchers("/api/admin/pagamentos/extrato").hasAnyAuthority("MASTER", "VOLUNTARIO", "ROLE_MASTER", "ROLE_VOLUNTARIO")

                        // 6. GESTÃO MASTER (Acesso restrito)

                        // Gestão de PIX (Criação/Edição/Exclusão - Protegido!)
                        .requestMatchers("/api/admin/configuracao-pix/**").hasAnyAuthority("MASTER", "ROLE_MASTER")

                        // Clínicas
                        .requestMatchers(HttpMethod.GET, "/api/admin/clinicas/**").hasAnyAuthority("MASTER", "VOLUNTARIO", "ROLE_MASTER", "ROLE_VOLUNTARIO")
                        .requestMatchers("/api/admin/clinicas/**").hasAnyAuthority("MASTER", "ROLE_MASTER")

                        // Voluntários e Controle de Sistema
                        .requestMatchers("/api/admin/voluntarios/**").hasAnyAuthority("MASTER", "ROLE_MASTER")
                        .requestMatchers(HttpMethod.PATCH, "/api/sistema/admin/toggle").hasAnyAuthority("MASTER", "ROLE_MASTER")

                        // 7. BLOQUEIO FINAL
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. LISTA DE ORIGENS PERMITIDAS
        // Mantemos o localhost para você trabalhar no seu PC e a URL do Render para a ONG
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://sistema-castracao-app.onrender.com"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 2. AJUSTE NOS HEADERS
        // Agora que usamos cookies, o header "Authorization" não é mais o único importante.
        // O navegador gerencia os cookies automaticamente.
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Accept", "X-Requested-With", "Origin"));

        // 3. PERMITIR CREDENCIAIS (O mais importante para HttpOnly)
        // Sem isso aqui ser 'true', o navegador se recusa a salvar o cookie que o Java envia.
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