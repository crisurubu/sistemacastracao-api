package com.projetoong.sistema_castracao.dto;

public record PerformanceClinicaDTO(
        Long id,
        String nome,
        String cnpj,
        Long totalAgendamentos,
        Long totalRealizados
) {}