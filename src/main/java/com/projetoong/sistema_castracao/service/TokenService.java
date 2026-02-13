package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Administrador;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    private final String SECRET_KEY = "SuaChaveSuperSecretaParaASegurancaDaOngTatuí2026";
    private final long EXPIRATION_TIME = 86400000; // 24 horas

    // 1. GERA O TOKEN (Você já tinha esse)
    public String gerarToken(Administrador admin) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(admin.getEmail())
                .claim("id", admin.getId())
                .claim("nome", admin.getNome())
                .claim("role", admin.getNivelAcesso().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. VALIDA E LÊ O TOKEN (Faltava este para o SecurityFilter funcionar)
    public String getSubject(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // Retorna o e-mail do admin
        } catch (Exception e) {
            // Se o token for falso, alterado ou expirado, cai aqui
            return null;
        }
    }
}