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
            System.out.println(">>> Tentativa de login recebida para: " + dados.email());

            String token = autenticacaoService.validarLogin(dados);

            Administrador admin = administradorRepository.findByEmail(dados.email()).get();

            System.out.println(">>> Login realizado com sucesso para: " + admin.getNome());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of(
                            "nome", admin.getNome(),
                            "email", admin.getEmail(),
                            "nivelAcesso", admin.getNivelAcesso()
                    )
            ));
        } catch (Exception e) {
            System.err.println(">>> ERRO NO LOGIN: " + e.getMessage());
            e.printStackTrace(); // Isso vai mostrar a linha exata onde falhou no seu Service
            return ResponseEntity.status(401).body(Map.of("erro", e.getMessage()));
        }
    }
}