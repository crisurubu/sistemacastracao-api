package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.dto.CadastroPetRecord;
import com.projetoong.sistema_castracao.service.CadastroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/cadastros") // Coloquei o 'S' para bater com o SecurityConfig e o Front
@CrossOrigin(origins = "http://localhost:5173")
public class CadastroController {

    @Autowired
    private CadastroService cadastroService;

    // MÉTODO QUE ESTAVA FALTANDO PARA O ALARME FUNCIONAR
    @GetMapping("/tutor/{id}")
    public ResponseEntity<?> buscarPorTutor(@PathVariable Long id) {
        // O service deve buscar no seu CadastroRepository usando o ID do tutor
        var lista = cadastroService.buscarHistoricoPorTutor(id);
        return ResponseEntity.ok(lista);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> cadastrar(
            @RequestPart("dados") CadastroPetRecord dados,
            @RequestPart("arquivo") MultipartFile arquivo) {
        try {
            cadastroService.cadastrar(dados, arquivo);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensagem", "Cadastro realizado com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}