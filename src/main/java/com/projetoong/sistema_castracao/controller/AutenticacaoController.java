package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.dto.LoginDTO;
import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private AdministradorRepository administradorRepository; // Precisamos disso para pegar os dados do admin

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dados) {
        try {
            // 1. O service valida e gera o token JWT
            String token = autenticacaoService.validarLogin(dados);

            // 2. Buscamos o administrador para enviar os dados que o React espera
            // .get() aqui é seguro porque o service já validou que o usuário existe
            Administrador admin = administradorRepository.findByEmail(dados.email()).get();

            // 3. Retornamos o pacote completo: Token + User
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of(
                            "nome", admin.getNome(),
                            "email", admin.getEmail(),
                            "nivelAcesso", admin.getNivelAcesso() // MASTER ou VOLUNTARIO
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("erro", e.getMessage()));
        }
    }
}