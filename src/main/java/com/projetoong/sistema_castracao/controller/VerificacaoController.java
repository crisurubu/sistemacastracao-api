package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/agendamentos") // Caminho que o seu React chamou no erro
@CrossOrigin(origins = "*") // Libera para o React (Vite) acessar sem erro de CORS
public class VerificacaoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping("/verificar/{hash}")
    public ResponseEntity<?> verificarGuia(@PathVariable String hash) {
        try {
            // Pede ao Service para buscar no banco pelo Hash
            Agendamento agendamento = agendamentoService.buscarPorHash(hash);
            return ResponseEntity.ok(agendamento);
        } catch (RuntimeException e) {
            // Se não existir o hash, retorna o erro 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Código Hash inválido ou não encontrado.");
        }
    }
}