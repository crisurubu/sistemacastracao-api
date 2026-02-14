package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.service.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/clinicas")
@CrossOrigin(origins = "http://localhost:5173")
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    // 1. SALVAR OU ATUALIZAR (O que resolve o problema de barrar o CNPJ)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PostMapping
    public ResponseEntity<Clinica> salvarOuAtualizar(@RequestBody Clinica clinica) {
        Clinica resultado = clinicaService.salvarOuAtualizar(clinica);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    // 2. VERIFICAR E PUXAR DADOS
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @GetMapping("/verificar/{cnpj}")
    public ResponseEntity<Map<String, Object>> verificarCnpj(@PathVariable String cnpj) {
        Map<String, Object> response = new HashMap<>();

        return clinicaService.buscarPorCnpj(cnpj)
                .map(clinica -> {
                    response.put("existe", true);
                    response.put("clinica", clinica);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("existe", false);
                    return ResponseEntity.ok(response);
                });
    }

    // 3. ATUALIZAR (Corrigido para usar o objeto existente do banco primeiro)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PutMapping("/{id}")
    public ResponseEntity<Clinica> atualizar(@PathVariable Long id, @RequestBody Clinica clinica) {
        // Buscamos a clínica pelo ID para garantir que ela existe antes de atualizar
        return clinicaService.buscarPorCnpj(clinica.getCnpj())
                .map(existente -> {
                    Clinica atualizada = clinicaService.atualizarExistente(existente, clinica);
                    return ResponseEntity.ok(atualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. CONCLUIR CASTRAÇÃO (Histórico de Vida)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER', 'CLINICA', 'ROLE_CLINICA')")
    @PatchMapping("/{id}/concluir-castracao")
    public ResponseEntity<Void> registrarCastracao(@PathVariable Long id) {
        clinicaService.registrarCastracaoConcluida(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER', 'VOLUNTARIO', 'ROLE_VOLUNTARIO')")
    @GetMapping
    public ResponseEntity<List<Clinica>> listar() {
        return ResponseEntity.ok(clinicaService.listarTodas());
    }

    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        clinicaService.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }
}