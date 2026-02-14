package com.projetoong.sistema_castracao.dto;

import java.util.List;

public record DashboardSummaryDTO(
        long totalPets,
        long tutoresAtivos,
        double arrecadacaoTotal,
        // Novos campos baseados nos status da tabela de Cadastros
        long totalAguardandoPagamento,
        long totalNaFila,
        long totalAgendados,
        long totalConcluidos,
        long totalVoluntariosAtivos,
        long totalVoluntariosInativos,
        List<FluxoFinanceiroDTO> fluxoFinanceiro,
        List<PerformanceClinicaDTO> topClinicas,
        List<EspecieDTO> distribuicaoEspecies
) {}