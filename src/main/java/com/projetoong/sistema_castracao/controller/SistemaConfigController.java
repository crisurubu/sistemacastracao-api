package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.SistemaConfig;
import com.projetoong.sistema_castracao.service.SistemaConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sistema")
public class SistemaConfigController {

    @Autowired
    private SistemaConfigService service;

    // ROTA PÚBLICA: O Front-end da Home usa essa aqui
    @GetMapping("/status")
    public ResponseEntity<SistemaConfig> getStatusPublico() {
        return ResponseEntity.ok(service.getConfig());
    }

    // ROTA MASTER: Só quem é ADMIN/MASTER pode acessar
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/admin/toggle")
    public ResponseEntity<SistemaConfig> toggleStatus(@RequestBody Map<String, Boolean> payload) {
        return ResponseEntity.ok(service.toggleCadastro(payload.get("aberto")));
    }
}