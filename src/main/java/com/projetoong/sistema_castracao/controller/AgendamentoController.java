package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<Agendamento> agendar(@RequestBody Map<String, String> payload) {
        Agendamento novo = agendamentoService.criarNovoAgendamento(
                Long.parseLong(payload.get("cadastroId")),
                payload.get("dataHora"),
                payload.get("local")
        );
        return ResponseEntity.ok(novo);
    }
    @PutMapping("/reagendar")
    public ResponseEntity<Agendamento> reagendar(@RequestBody Map<String, String> payload) {
        // Recebe o ID do agendamento, não o do cadastro, pois a linha na tabela já existe
        Long agendamentoId = Long.parseLong(payload.get("agendamentoId"));
        String novaDataHora = payload.get("dataHora");
        String novoLocal = payload.get("local");

        Agendamento atualizado = agendamentoService.reagendar(agendamentoId, novaDataHora, novoLocal);
        return ResponseEntity.ok(atualizado);
    }
    @GetMapping("/pendentes")
    public ResponseEntity<List<Agendamento>> listarPendentes() {
        // O Controller pede para o Service e entrega para o React
        List<Agendamento> lista = agendamentoService.listarAgendamentosPendentes();
        return ResponseEntity.ok(lista);
    }
    // No seu AgendamentoController.java


}
