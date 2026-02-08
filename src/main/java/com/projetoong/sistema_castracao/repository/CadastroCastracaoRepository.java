package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CadastroCastracaoRepository extends JpaRepository<CadastroCastracao, Long> {

    List<CadastroCastracao> findByTutor(Tutor tutor);
    // O IgnoreCase evita erros se o banco salvar "na_fila" ou "NA_FILA"
    List<CadastroCastracao> findByStatusProcessoIgnoreCase(String status);
}