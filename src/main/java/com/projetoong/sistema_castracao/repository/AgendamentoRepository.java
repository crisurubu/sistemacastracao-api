package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.dto.PerformanceClinicaDTO; // Importe seu record aqui
import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    Optional<Agendamento> findByCodigoHash(String hash);
    List<Agendamento> findByRealizadoFalseOrderByDataHoraAsc();
    List<Agendamento> findByClinicaAndRealizadoFalseOrderByDataHoraAsc(Clinica clinica);
    long countByClinicaAndRealizadoTrue(Clinica clinica);
    long countByClinicaIdAndRealizadoTrue(Long clinicaId);
    long countByRealizadoFalse();

    // --- DASHBOARD ADMIN COM SQL NATIVO ---
    @Query(value = """
        SELECT 
            c.id AS id, 
            c.nome AS nome, 
            c.cnpj AS cnpj, 
            COUNT(a.id) AS totalEnviados, 
            SUM(CASE WHEN a.realizado = true THEN 1 ELSE 0 END) AS totalRealizados
        FROM clinicas c
        LEFT JOIN agendamentos a ON c.id = a.clinica_id
        GROUP BY c.id, c.nome, c.cnpj
        ORDER BY totalRealizados DESC
        """, nativeQuery = true)
    List<Object[]> findPerformanceClinicasRaw();
    // Busca o agendamento pelo ID do cadastro vinculado
    Optional<Agendamento> findByCadastroId(Long cadastroId);

    // Se quiser buscar todos os agendamentos de um tutor (opcional)
    List<Agendamento> findByCadastroTutorId(Long tutorId);
}