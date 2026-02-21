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

        String path = request.getRequestURI();

        // Use .contains para garantir que ele pegue a rota independente de prefixos
        if (path.contains("/api/cadastros/tutor") || path.contains("/api/tutores")) {
            System.out.println("LIBERANDO ROTA PELO FILTRO: " + path); // Isso vai aparecer no seu terminal do IntelliJ
            filterChain.doFilter(request, response);
            return;
        }

        var token = recuperarToken(request);

        if (token != null) {
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
        // 1. TENTA RECUPERAR DO COOKIE (Novo padrão HttpOnly)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }

        // 2. MANTÉM A BUSCA NO HEADER (Caso você ainda precise testar pelo Insomnia/Postman)
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }
}