package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.dto.FluxoFinanceiroDTO;
import com.projetoong.sistema_castracao.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    // --- 1. QUERIES DE DASHBOARD (Soma e Fluxo) ---
    @Query("SELECT COALESCE(SUM(p.valorContribuicao), 0.0) FROM Pagamento p WHERE p.confirmado = true")
    Double sumAprovados();

    @Query("SELECT new com.projetoong.sistema_castracao.dto.FluxoFinanceiroDTO(" +
            "TO_CHAR(p.cadastro.dataSolicitacao, 'Mon'), " +
            "SUM(CASE WHEN p.confirmado = true THEN p.valorContribuicao ELSE 0.0 END), " +
            "SUM(CASE WHEN p.confirmado = false THEN p.valorContribuicao ELSE 0.0 END)) " +
            "FROM Pagamento p " +
            "GROUP BY TO_CHAR(p.cadastro.dataSolicitacao, 'Mon'), " +
            "EXTRACT(MONTH FROM p.cadastro.dataSolicitacao) " +
            "ORDER BY EXTRACT(MONTH FROM p.cadastro.dataSolicitacao) ASC")
    List<FluxoFinanceiroDTO> findFluxoFinanceiroMensal();

    // --- 2. QUERIES DE ALARMES E AUDITORIA (Resolve o erro do AlarmeService) ---

    // Este é o método que estava faltando e causando erro no Service!
    @Query("SELECT p FROM Pagamento p WHERE p.confirmado = true AND p.dataConfirmacao >= :inicioDia")
    List<Pagamento> findAprovadosHoje(@Param("inicioDia") LocalDateTime inicioDia);

    @Query("SELECT COUNT(p) FROM Pagamento p WHERE p.confirmado = false AND p.cadastro.dataSolicitacao <= :dataLimite")
    long countAlertasCriticos(@Param("dataLimite") LocalDateTime dataLimite);

    @Query("SELECT p FROM Pagamento p WHERE p.confirmado = false AND p.cadastro.dataSolicitacao <= :dataLimite")
    List<Pagamento> findAlertasCriticos(@Param("dataLimite") LocalDateTime dataLimite);

    // --- 3. QUERIES DE EXTRATO (Histórico de Vida) ---
    List<Pagamento> findByConfirmadoTrueOrderByDataConfirmacaoDesc();

    List<Pagamento> findByAprovadoPorIdAndConfirmadoTrue(Long voluntarioId);

    List<Pagamento> findByContaDestinoIdAndConfirmadoTrue(Long pixId);

    // --- 4. CONSULTAS BÁSICAS ---
    List<Pagamento> findByConfirmadoFalse();

    long countByConfirmadoFalse();
}