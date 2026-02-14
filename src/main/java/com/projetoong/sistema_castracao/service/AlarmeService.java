package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.ConfiguracaoPix;
import com.projetoong.sistema_castracao.model.Pagamento;
import com.projetoong.sistema_castracao.repository.ConfiguracaoPixRepository;
import com.projetoong.sistema_castracao.repository.PagamentoRepository;
import com.projetoong.sistema_castracao.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Importe o Optional

@Service
public class AlarmeService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ConfiguracaoPixRepository pixRepository;

    public List<Map<String, String>> gerarRelatorioAlarmes() {
        List<Map<String, String>> alarmes = new ArrayList<>();

        // 1. REGRA FINANCEIRA
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(15);
        long pendentesCriticos = pagamentoRepository.countAlertasCriticos(dataLimite);
        if (pendentesCriticos > 0) {
            alarmes.add(criarAlarme("CRÍTICO", pendentesCriticos + " cadastros aguardando pagamento há +15 dias", "ALTA", "N/A"));
        }

        // 2. REGRA OPERACIONAL
        long naFila = petRepository.countByStatusFila();
        if (naFila > 5) {
            alarmes.add(criarAlarme("OPERACIONAL", "Fila crítica: " + naFila + " pets aguardando", "MEDIA", "N/A"));
        }

        // 3. REGRA: Alerta de PIX alterado (USANDO OPTIONAL CORRETAMENTE)
        LocalDateTime ontem = LocalDateTime.now().minusDays(1);
        Optional<ConfiguracaoPix> pixRecente = pixRepository.findTopByDataCriacaoAfterOrderByDataCriacaoDesc(ontem);

        if (pixRecente.isPresent()) { // Se existir algo dentro do Optional
            alarmes.add(criarAlarme(
                    "AUDITORIA",
                    "⚠️ A conta PIX de destino foi alterada recentemente!",
                    "ALTA",
                    "Sistema Master"
            ));
        }

        // 4. REGRA: Resumo de quem trabalhou hoje
        // Importante: Zeramos horas/minutos para pegar o dia todo
        LocalDateTime inicioHoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<Pagamento> aprovadosHoje = pagamentoRepository.findAprovadosHoje(inicioHoje);

        if (aprovadosHoje != null && !aprovadosHoje.isEmpty()) {
            alarmes.add(criarAlarme(
                    "AUDITORIA",
                    "Hoje foram confirmadas " + aprovadosHoje.size() + " castrações.",
                    "INFO",
                    "Ver no Extrato"
            ));
        }

        return alarmes;
    }

    private Map<String, String> criarAlarme(String tipo, String mensagem, String prioridade, String responsavel) {
        Map<String, String> alarme = new HashMap<>();
        alarme.put("tipo", tipo);
        alarme.put("mensagem", mensagem);
        alarme.put("prioridade", prioridade);
        alarme.put("responsavel", responsavel);
        return alarme;
    }
}