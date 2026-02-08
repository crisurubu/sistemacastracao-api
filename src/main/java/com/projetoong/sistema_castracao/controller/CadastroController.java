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
@RequestMapping("/api/cadastro")
@CrossOrigin(origins = "*")
public class CadastroController {

    @Autowired
    private CadastroService cadastroService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> cadastrar(
            @RequestPart("dados") CadastroPetRecord dados,
            @RequestPart("arquivo") MultipartFile arquivo) {

        try {
            // Chamamos o service que agora faz o upload para a nuvem
            cadastroService.cadastrar(dados, arquivo);

            // Retornamos 201 (Created) e uma mensagem de sucesso
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensagem", "Cadastro e comprovante realizados com sucesso na nuvem!"));

        } catch (RuntimeException e) {
            // Se o Cloudinary falhar ou o banco der erro, avisamos o Front
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}