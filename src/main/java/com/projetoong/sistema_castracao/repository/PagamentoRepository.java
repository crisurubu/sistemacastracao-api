package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query("SELECT COALESCE(SUM(p.valorContribuicao), 0.0) FROM Pagamento p WHERE p.confirmado = true")
    Double sumAprovados();

    @Query(value = "SELECT TO_CHAR(data_confirmacao, 'Mon') as mes, " +
            "SUM(valor_contribuicao) as arrecadado, " +
            "COUNT(*) as totalConfirmados " +
            "FROM pagamentos WHERE confirmado = true " +
            "GROUP BY mes, TO_CHAR(data_confirmacao, 'MM') " +
            "ORDER BY TO_CHAR(data_confirmacao, 'MM') ASC", nativeQuery = true)
    List<Map<String, Object>> findFluxoMensal();

    // --- PARA A GESTÃO FINANCEIRA (Continua vendo TUDO) ---
    List<Pagamento> findByConfirmadoFalse();
    long countByConfirmadoFalse();

    // 1. Busca os registros atrasados (Olhando para a data da solicitação no cadastro)
    @Query("SELECT p FROM Pagamento p WHERE p.confirmado = false " +
            "AND p.cadastro.dataSolicitacao <= :dataLimite")
    List<Pagamento> findAlertasCriticos(@Param("dataLimite") LocalDateTime dataLimite);

    // 2. Conta para o balão da Sidebar (Olhando para a data da solicitação no cadastro)
    @Query("SELECT COUNT(p) FROM Pagamento p WHERE p.confirmado = false " +
            "AND p.cadastro.dataSolicitacao <= :dataLimite")
    long countAlertasCriticos(@Param("dataLimite") LocalDateTime dataLimite);

}