package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.dto.LoginDTO;
import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// O CORS agora deve ser configurado em uma classe global para permitir 'allowCredentials'
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private AdministradorRepository administradorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dados) {
        try {
            System.out.println(">>> Tentativa de login recebida para: " + dados.email());

            // 1. Valida e gera o token string
            String token = autenticacaoService.validarLogin(dados);
            Administrador admin = administradorRepository.findByEmail(dados.email()).get();

            // 2. Cria o Cookie HttpOnly (O cofre do token)
            ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                    .httpOnly(true)
                    .secure(true)    // No Render (HTTPS) deve ser true. Se testar em localhost sem HTTPS, mude para false temporariamente.
                    .path("/")
                    .maxAge(24 * 60 * 60) // 24 horas
                    .sameSite("None")    // Crucial para CORS entre domínios diferentes
                    .build();

            System.out.println(">>> Login realizado com sucesso para: " + admin.getNome());

            // 3. Retorna apenas os dados do USER. O Token vai no Header como Cookie.
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of(
                            "user", Map.of(
                                    "nome", admin.getNome(),
                                    "email", admin.getEmail(),
                                    "nivelAcesso", admin.getNivelAcesso()
                            )
                    ));
        } catch (Exception e) {
            System.err.println(">>> ERRO NO LOGIN: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Para deslogar, limpamos o cookie enviando um com tempo zero
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logout realizado com sucesso"));
    }
}