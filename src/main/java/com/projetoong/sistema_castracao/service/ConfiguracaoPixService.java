package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.ConfiguracaoPix;
import com.projetoong.sistema_castracao.repository.ConfiguracaoPixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfiguracaoPixService {

    @Autowired
    private ConfiguracaoPixRepository repository;

    @Transactional
    public ConfiguracaoPix salvarNovaChave(ConfiguracaoPix novaChave) {
        // 1. Desativa as chaves antigas
        repository.desativarTodasAsChaves();

        // 2. Prepara os dados conforme o seu Model
        novaChave.setAtivo(true);

        // O erro era aqui: O nome correto do seu método é setDataCriacao
        novaChave.setDataCriacao(LocalDateTime.now());

        // 3. Salva no banco (incluindo o valorTaxa)
        return repository.save(novaChave);
    }

    public ConfiguracaoPix buscarChaveAtiva() {
        return repository.findByAtivoTrue()
                .orElseThrow(() -> new RuntimeException("Nenhuma configuração PIX ativa encontrada!"));
    }

    public List<ConfiguracaoPix> listarTodasAsChaves() {
        return repository.findAll();
    }
}