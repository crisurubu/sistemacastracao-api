package com.projetoong.sistema_castracao.config;

import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdministradorRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- INÍCIO DA MELHORIA ---
        // Convertemos para minúsculo para evitar que "API" seja diferente de "api"
        String path = request.getRequestURI().toLowerCase();

        // Lista de rotas que não devem passar pela verificação de Token
        // Adicione aqui qualquer rota que seja 100% aberta ao público
        if (path.contains("/api/auth/login") ||
                path.contains("/api/cadastros/tutor") ||
                path.contains("/api/public") ||
                path.contains("/api/sistema/status")) {

            filterChain.doFilter(request, response);
            return;
        }
        // --- FIM DA MELHORIA ---

        var token = recuperarToken(request);

        if (token != null) {
            // ... (resto do seu código de validação de token permanece igual)
            var login = tokenService.getSubject(token);
            System.out.println("FILTRO - Email extraído do Token: [" + login + "]"); // LOG AQUI

            var administrador = repository.findByEmail(login).orElse(null);

            if (administrador != null) {
                String roleName = administrador.getNivelAcesso().name();
                System.out.println("FILTRO - Usuário encontrado: " + administrador.getNome() + " | Role: " + roleName); // LOG AQUI

                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + roleName),
                        new SimpleGrantedAuthority(roleName)
                );

                var authentication = new UsernamePasswordAuthenticationToken(
                        administrador, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("FILTRO - ERRO: Usuário não encontrado no banco para o e-mail: " + login); // LOG AQUI
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // Log para ver se QUALQUER cookie está chegando
                System.out.println("Cookie recebido: " + cookie.getName());
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }
        // ... resto do método


        // 2. MANTÉM A BUSCA NO HEADER (Caso você ainda precise testar pelo Insomnia/Postman)
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }
}