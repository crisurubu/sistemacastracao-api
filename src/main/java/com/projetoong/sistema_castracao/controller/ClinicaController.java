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
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    // 1. SALVAR (Igual ao fluxo de voluntários)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PostMapping
    public ResponseEntity<Clinica> salvar(@RequestBody Clinica clinica) {
        // Garantimos que a lógica de "se existir CNPJ, atualize, se não, crie" esteja no Service
        Clinica resultado = clinicaService.salvarOuAtualizar(clinica);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    // 2. VERIFICAR CNPJ (Crucial para o modo de edição automática no React)
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

    // 3. ATUALIZAÇÃO VIA ID (Put limpo)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PutMapping("/{id}")
    public ResponseEntity<Clinica> atualizar(@PathVariable Long id, @RequestBody Clinica clinica) {
        // No fluxo correto, o ID da URL manda no processo
        return clinicaService.buscarPorId(id)
                .map(existente -> {
                    // O Service deve copiar os novos campos (cep, bairro...) para o existente
                    Clinica atualizada = clinicaService.atualizarExistente(existente, clinica);
                    return ResponseEntity.ok(atualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. MÉTODOS DE APOIO E LISTAGEM
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
    public ResponseEntity<Map<String, Object>> alternarStatus(@PathVariable Long id) {
        clinicaService.alternarStatus(id);

        // Opcional: Retornar o novo status para o React atualizar o botão instantaneamente
        boolean novoStatus = clinicaService.buscarPorId(id).get().getAdministrador().isAtivo();
        Map<String, Object> response = new HashMap<>();
        response.put("ativo", novoStatus);
        response.put("message", "Status alterado com sucesso");

        return ResponseEntity.ok(response);
    }
}