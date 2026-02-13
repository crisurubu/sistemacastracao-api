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

@RestController
@RequestMapping("/api/admin/clinicas")
@CrossOrigin(origins = "http://localhost:5173")
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    // Apenas o MASTER (ONG) pode cadastrar novas unidades
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PostMapping
    public ResponseEntity<Clinica> cadastrar(@RequestBody Clinica clinica) {
        Clinica novaClinica = clinicaService.salvar(clinica);
        return new ResponseEntity<>(novaClinica, HttpStatus.CREATED);
    }

    // Listagem para o Dashboard da ONG (Ranking de produtividade)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER', 'VOLUNTARIO', 'ROLE_VOLUNTARIO')")
    @GetMapping
    public ResponseEntity<List<Clinica>> listar() {
        List<Clinica> clinicas = clinicaService.listarTodas();
        return ResponseEntity.ok(clinicas);
    }

    // Verificação de CNPJ para evitar duplicidade no cadastro
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @GetMapping("/verificar/{cnpj}")
    public ResponseEntity<Map<String, Boolean>> verificarCnpj(@PathVariable String cnpj) {
        boolean existe = clinicaService.existePorCnpj(cnpj);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    // Atualização de dados cadastrais da clínica
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PutMapping("/{id}")
    public ResponseEntity<Clinica> atualizar(@PathVariable Long id, @RequestBody Clinica clinica) {
        Clinica atualizada = clinicaService.atualizar(id, clinica);
        return ResponseEntity.ok(atualizada);
    }

    // Ativar ou desativar uma clínica (bloqueio de acesso)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        clinicaService.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }

    // Registro de conclusão de castração (Alimenta o Histórico de Vida e o Selo)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER', 'CLINICA', 'ROLE_CLINICA')")
    @PatchMapping("/{id}/concluir-castracao")
    public ResponseEntity<Void> registrarCastracao(@PathVariable Long id) {
        clinicaService.registrarCastracaoConcluida(id);
        return ResponseEntity.ok().build();
    }
}