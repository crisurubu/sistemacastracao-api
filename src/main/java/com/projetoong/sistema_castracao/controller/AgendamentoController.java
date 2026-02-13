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
@CrossOrigin(origins = "http://localhost:5173")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<Agendamento> agendar(@RequestBody Map<String, String> payload) {
        // Agora pegamos o clinicaId do payload vindo do React
        Agendamento novo = agendamentoService.criarNovoAgendamento(
                Long.parseLong(payload.get("cadastroId")),
                payload.get("dataHora"),
                Long.parseLong(payload.get("clinicaId")) // MUDANÇA AQUI: De String para Long
        );
        return ResponseEntity.ok(novo);
    }

    @PutMapping("/reagendar")
    public ResponseEntity<Agendamento> reagendar(@RequestBody Map<String, String> payload) {
        Long agendamentoId = Long.parseLong(payload.get("agendamentoId"));
        String novaDataHora = payload.get("dataHora");
        // Se no reagendamento você também permitir trocar a clínica:
        Long novaClinicaId = Long.parseLong(payload.get("clinicaId"));

        // Ajuste o seu método reagendar no Service para aceitar o Long novaClinicaId também
        Agendamento atualizado = agendamentoService.reagendar(agendamentoId, novaDataHora, novaClinicaId);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Agendamento>> listarPendentes() {
        List<Agendamento> lista = agendamentoService.listarAgendamentosPendentes();
        return ResponseEntity.ok(lista);
    }
}