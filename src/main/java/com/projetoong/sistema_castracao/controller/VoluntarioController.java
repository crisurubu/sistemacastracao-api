package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.Voluntario;
import com.projetoong.sistema_castracao.service.VoluntarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/voluntarios")
@CrossOrigin(origins = "http://localhost:5173") // Mantendo o padrão do seu Front
public class VoluntarioController {

    @Autowired
    private VoluntarioService voluntarioService;

    // Apenas o MASTER pode gerenciar a equipe de voluntários
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PostMapping
    public ResponseEntity<Voluntario> salvar(@RequestBody Voluntario voluntario) {
        Voluntario salvo = voluntarioService.salvarOuAtualizar(voluntario);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    // Listagem de voluntários (Master e os próprios voluntários podem ver a equipe)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER', 'VOLUNTARIO', 'ROLE_VOLUNTARIO')")
    @GetMapping
    public ResponseEntity<List<Voluntario>> listar() {
        List<Voluntario> voluntarios = voluntarioService.listarTodos();
        return ResponseEntity.ok(voluntarios);
    }

    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @GetMapping("/verificar/{cpf}")
    public ResponseEntity<Map<String, Object>> verificarCpf(@PathVariable String cpf) {
        Optional<Voluntario> voluntarioOpt = voluntarioService.buscarPorCpf(cpf);

        if (voluntarioOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "existe", true,
                    "voluntario", voluntarioOpt.get()
            ));
        }
        return ResponseEntity.ok(Map.of("existe", false));
    }

    // Ativar ou desativar acesso de um voluntário (Bloqueio)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        voluntarioService.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }
    // Adicione este método abaixo do método listar()
    @PreAuthorize("hasAnyAuthority('MASTER', 'ROLE_MASTER')")
    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> buscarPorId(@PathVariable Long id) {
        return voluntarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}