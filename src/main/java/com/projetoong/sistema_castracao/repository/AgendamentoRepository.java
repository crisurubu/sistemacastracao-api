package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    // Para a verificação do veterinário no futuro
    Optional<Agendamento> findByCodigoHash(String hash);
    List<Agendamento> findByRealizadoFalseOrderByDataHoraAsc();
}