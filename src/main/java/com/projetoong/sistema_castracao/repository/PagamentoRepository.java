package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.dto.FluxoFinanceiroDTO; // Importe seu Record
import com.projetoong.sistema_castracao.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query("SELECT COALESCE(SUM(p.valorContribuicao), 0.0) FROM Pagamento p WHERE p.confirmado = true")
    Double sumAprovados();

    // NOVA QUERY: Agora batendo com o gráfico "Financeiro vs Rejeitados"
    // Usamos JPQL para converter o mês em String (funciona bem no H2/Postgres)
    @Query("SELECT new com.projetoong.sistema_castracao.dto.FluxoFinanceiroDTO(" +
            "TO_CHAR(p.cadastro.dataSolicitacao, 'Mon'), " +
            "SUM(CASE WHEN p.confirmado = true THEN p.valorContribuicao ELSE 0.0 END), " +
            "SUM(CASE WHEN p.confirmado = false THEN p.valorContribuicao ELSE 0.0 END)) " +
            "FROM Pagamento p " +
            "GROUP BY TO_CHAR(p.cadastro.dataSolicitacao, 'Mon'), " +
            "EXTRACT(MONTH FROM p.cadastro.dataSolicitacao) " +
            "ORDER BY EXTRACT(MONTH FROM p.cadastro.dataSolicitacao) ASC")
    List<FluxoFinanceiroDTO> findFluxoFinanceiroMensal();

    // --- MANTENHA O RESTANTE DO SEU CÓDIGO ---
    List<Pagamento> findByConfirmadoFalse();
    long countByConfirmadoFalse();

    @Query("SELECT p FROM Pagamento p WHERE p.confirmado = false AND p.cadastro.dataSolicitacao <= :dataLimite")
    List<Pagamento> findAlertasCriticos(@Param("dataLimite") LocalDateTime dataLimite);

    @Query("SELECT COUNT(p) FROM Pagamento p WHERE p.confirmado = false AND p.cadastro.dataSolicitacao <= :dataLimite")
    long countAlertasCriticos(@Param("dataLimite") LocalDateTime dataLimite);
}