package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    // NOVO: Busca direta pelo e-mail da clínica (O que o Controller precisa agora)
    Optional<Clinica> findByEmail(String email);

    // Busca a clínica através do login do Administrador
    Optional<Clinica> findByAdministrador(Administrador administrador);

    // Busca por CNPJ para evitar cadastros duplicados
    Optional<Clinica> findByCnpj(String cnpj);

    // Busca clínicas ordenadas por produtividade (Ranking de Selos)
    List<Clinica> findAllByOrderByTotalCastracoesDesc();
}