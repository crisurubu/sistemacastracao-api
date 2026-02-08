package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.repository.PagamentoRepository;
import com.projetoong.sistema_castracao.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlarmeService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PetRepository petRepository;

    public List<Map<String, String>> gerarRelatorioAlarmes() {
        List<Map<String, String>> alarmes = new ArrayList<>();

        // Regra 1: Financeiro Crítico (+15 dias desde a SOLICITAÇÃO)
        // O Repository agora olha para p.cadastro.dataSolicitacao
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(15);
        long pendentesCriticos = pagamentoRepository.countAlertasCriticos(dataLimite);

        if (pendentesCriticos > 0) {
            alarmes.add(criarAlarme(
                    "CRÍTICO",
                    pendentesCriticos + " cadastros aguardando pagamento há +15 dias",
                    "ALTA"
            ));
        }

        // Regra 2: Operacional (Fila de Castração)
        long naFila = petRepository.countByStatusFila();
        if (naFila > 5) {
            alarmes.add(criarAlarme("OPERACIONAL", "Fila crítica: " + naFila + " pets aguardando", "MEDIA"));
        }

        return alarmes;
    }

    private Map<String, String> criarAlarme(String tipo, String mensagem, String prioridade) {
        // Usamos HashMap se precisar que o Map seja mutável,
        // mas Map.of é ótimo para performance aqui
        return Map.of(
                "tipo", tipo,
                "mensagem", mensagem,
                "prioridade", prioridade
        );
    }
}