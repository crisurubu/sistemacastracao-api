package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.Administrador; // Importamos para limpar o código
import com.projetoong.sistema_castracao.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/agendamentos")
@CrossOrigin(origins = "http://localhost:5173")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // Método para extrair o e-mail de forma limpa e evitar repetição de código
    private String extrairEmail(Authentication auth) {
        if (auth.getPrincipal() instanceof Administrador) {
            return ((Administrador) auth.getPrincipal()).getEmail();
        }
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<Agendamento> agendar(@RequestBody Map<String, String> payload, Authentication auth) {
        String email = extrairEmail(auth);

        Agendamento novo = agendamentoService.criarNovoAgendamento(
                Long.parseLong(payload.get("cadastroId")),
                payload.get("dataHora"),
                Long.parseLong(payload.get("clinicaId")),
                email // Enviando String
        );
        return ResponseEntity.ok(novo);
    }

    @PutMapping("/reagendar")
    public ResponseEntity<Agendamento> reagendar(@RequestBody Map<String, String> payload, Authentication auth) {
        String email = extrairEmail(auth);

        Agendamento atualizado = agendamentoService.reagendar(
                Long.parseLong(payload.get("agendamentoId")),
                payload.get("dataHora"),
                Long.parseLong(payload.get("clinicaId")),
                email // AGORA TAMBÉM ENVIA STRING
        );
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Agendamento>> listarPendentes() {
        return ResponseEntity.ok(agendamentoService.listarAgendamentosPendentes());
    }
}