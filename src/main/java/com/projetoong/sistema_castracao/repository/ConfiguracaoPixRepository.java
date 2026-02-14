package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.ConfiguracaoPix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfiguracaoPixRepository extends JpaRepository<ConfiguracaoPix, Long> {

    // Busca a chave que o Tutor vai ver no formulário React
    Optional<ConfiguracaoPix> findByAtivoTrue();

    // Desativa todas as chaves antes de ativar a nova
    @Modifying
    @Query("UPDATE ConfiguracaoPix c SET c.ativo = false WHERE c.ativo = true")
    void desativarTodasAsChaves();

    Optional<ConfiguracaoPix> findTopByDataCriacaoAfterOrderByDataCriacaoDesc(LocalDateTime data);
}