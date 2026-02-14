package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.dto.DashboardSummaryDTO;
import com.projetoong.sistema_castracao.dto.PerformanceClinicaDTO;
import com.projetoong.sistema_castracao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired private PetRepository petRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private AgendamentoRepository agendamentoRepository;
    // Injetamos o repositório que tem o statusProcesso
    @Autowired private CadastroRepository cadastroRepository;
    @Autowired
    private VoluntarioService voluntarioService; // Injeção aqui!

    public DashboardSummaryDTO getResumoCompleto() {
        // 1. Buscamos os dados dos Cards (Geral)
        long totalPets = petRepository.count();
        long tutoresAtivos = tutorRepository.count();
        double arrecadacaoTotal = pagamentoRepository.sumAprovados() != null ? pagamentoRepository.sumAprovados() : 0.0;

        // 2. BUSCA DOS STATUS REAIS NA TABELA DE CADASTROS (O pulo do gato)
        long aguardandoPgto = cadastroRepository.countByStatusProcesso("AGUARDANDO_PAGAMENTO");
        long naFila = cadastroRepository.countByStatusProcesso("NA_FILA");
        long agendados = cadastroRepository.countByStatusProcesso("AGENDADO");
        long concluidos = cadastroRepository.countByStatusProcesso("CONCLUIDO");

        // 3. Listas para os Gráficos
        var distribuicaoEspecies = petRepository.findEspeciesCount();
        var fluxoFinanceiro = pagamentoRepository.findFluxoFinanceiroMensal();

        // Mapeamento da Performance das Clínicas
        List<Object[]> resultadosBrutos = agendamentoRepository.findPerformanceClinicasRaw();
        List<PerformanceClinicaDTO> performanceClinicas = resultadosBrutos.stream().map(coluna -> new PerformanceClinicaDTO(
                ((Number) coluna[0]).longValue(),
                (String) coluna[1],
                (String) coluna[2],
                ((Number) coluna[3]).longValue(),
                ((Number) coluna[4]).longValue()
        )).collect(Collectors.toList());

        // 4. Retornamos o Record com os novos campos (incluindo Voluntários)
        return new DashboardSummaryDTO(
                totalPets,
                tutoresAtivos,
                arrecadacaoTotal,
                aguardandoPgto,
                naFila,
                agendados,
                concluidos,
                // ADICIONE ESTES DOIS AQUI:
                voluntarioService.contarAtivos(true),  // totalVoluntariosAtivos
                voluntarioService.contarAtivos(false), // totalVoluntariosInativos
                // -------------------------
                fluxoFinanceiro,
                performanceClinicas,
                distribuicaoEspecies
        );
    }
}