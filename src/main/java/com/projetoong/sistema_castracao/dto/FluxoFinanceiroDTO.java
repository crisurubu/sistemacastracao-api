package com.projetoong.sistema_castracao.dto;

public record FluxoFinanceiroDTO(
        String mes,
        Double confirmados,  // Use Double (objeto) em vez de double (primitivo)
        Double rejeitados    // Use Double (objeto) em vez de double (primitivo)
) {}