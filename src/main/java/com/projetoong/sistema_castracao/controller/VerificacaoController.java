package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publico") // Alinhado com o React e com o SecurityConfig
public class VerificacaoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping("/verificar/{hash}")
    public ResponseEntity<?> verificarGuia(@PathVariable String hash) {
        try {
            Agendamento agendamento = agendamentoService.buscarPorHash(hash);
            // Retornamos um objeto limpo para o front-end
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Código Hash inválido ou não encontrado.");
        }
    }
}