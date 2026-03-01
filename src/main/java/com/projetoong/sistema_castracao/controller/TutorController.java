package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.dto.TutorDTO;
import com.projetoong.sistema_castracao.model.Tutor;
import com.projetoong.sistema_castracao.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    // Endpoint para o Botão "Ver Histórico" (Admin)
    @GetMapping("/{id}")
    public TutorDTO buscarPorId(@PathVariable Long id) {
        return tutorService.buscarPorId(id);
    }

    // Endpoint para a Consulta Pública (CPF)
    @GetMapping("/historico/{cpf}")
    public Tutor buscarPorCpf(@PathVariable String cpf) {
        return tutorService.buscarPorCpf(cpf);
    }
    // Endpoint específico para o Formulário de Cadastro (Engenharia Reversa)
    // Não lança erro 500 se não achar, apenas devolve 404 para o React saber que é novo
    // Este é o método que o React vai chamar na Etapa 2
    @GetMapping("/consultar/{cpf:.+}")
    public ResponseEntity<Tutor> consultarParaCadastro(@PathVariable String cpf) {
        return tutorService.consultarParaCadastro(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ... outros imports

    @GetMapping("/verificar-email")
    public ResponseEntity<?> verificarEmail(@RequestParam String email) {
        return tutorService.buscarPorEmail(email)
                .map(tutor -> {
                    // Se achou, devolve que existe e quem é o dono (CPF)
                    java.util.Map<String, Object> response = new java.util.HashMap<>();
                    response.put("exists", true);
                    response.put("cpfOwner", tutor.getCpf());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    // Se não achou, devolve que não existe
                    java.util.Map<String, Object> response = new java.util.HashMap<>();
                    response.put("exists", false);
                    return ResponseEntity.ok(response);
                });
    }



}