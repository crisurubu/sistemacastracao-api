package com.projetoong.sistema_castracao.controller;

import com.projetoong.sistema_castracao.model.ConfiguracaoPix;
import com.projetoong.sistema_castracao.service.ConfiguracaoPixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// AJUSTADO: Agora o caminho bate com o SecurityConfig que você postou antes
@RequestMapping("/api/admin/configuracao-pix")
public class ConfiguracaoPixController {

    @Autowired
    private ConfiguracaoPixService pixService;

    /**
     * Endpoint para o MASTER cadastrar uma nova chave.
     * Rota completa: POST /api/admin/configuracao-pix/cadastrar
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<ConfiguracaoPix> cadastrar(@RequestBody ConfiguracaoPix novaChave) {
        ConfiguracaoPix salva = pixService.salvarNovaChave(novaChave);
        return ResponseEntity.ok(salva);
    }

    /**
     * Endpoint para buscar a chave ativa.
     * Rota completa: GET /api/admin/configuracao-pix/ativa
     */
    @GetMapping("/ativa")
    public ResponseEntity<ConfiguracaoPix> buscarAtiva() {
        try {
            return ResponseEntity.ok(pixService.buscarChaveAtiva());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Histórico de chaves para auditoria do MASTER.
     * Rota completa: GET /api/admin/configuracao-pix/historico
     */
    @GetMapping("/historico")
    public List<ConfiguracaoPix> listarTodas() {
        return pixService.listarTodasAsChaves();
    }
}