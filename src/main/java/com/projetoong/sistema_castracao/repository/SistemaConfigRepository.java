package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.SistemaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SistemaConfigRepository extends JpaRepository<SistemaConfig, Long> {
}