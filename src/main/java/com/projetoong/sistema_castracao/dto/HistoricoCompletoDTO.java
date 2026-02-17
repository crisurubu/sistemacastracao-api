package com.projetoong.sistema_castracao.dto;

import java.time.LocalDateTime;

public record HistoricoCompletoDTO(
        // --- DADOS DO TUTOR ---
        Long tutorId,
        String tutorNome,
        String tutorCpf,
        String tutorEmail,
        String tutorWhatsapp,
        String tutorLogradouro,
        String tutorNumero,
        String tutorBairro,
        String tutorCidade,
        String tutorEnderecoCompleto,

        // --- DADOS DO PET ---
        Long petId,
        String petNome,
        String petEspecie,
        String petSexo,
        String petIdade,
        boolean petVacinado,
        boolean petOperouAntes,
        String petMedicamentos,

        // --- DADOS DO PROCESSO (CADASTRO) ---
        Long cadastroId,
        LocalDateTime dataSolicitacao,
        String statusProcesso,

        // --- DADOS DO AGENDAMENTO (O CARIMBO) ---
        Long agendamentoId,
        LocalDateTime dataHoraAgendamento,
        String localAgendamento,
        String codigoHash,
        boolean realizado,
        LocalDateTime dataRegistroAgendamento,
        String agendadorNome,

        // --- DADOS DA CLÍNICA (A EXECUTORA) ---
        Long clinicaId,
        String clinicaNome,
        String clinicaCnpj,
        String clinicaCrmv,
        String clinicaTelefone,
        String clinicaEndereco,
        String clinicaEmail,
        int clinicaTotalCastracoes,
        String clinicaSelo
) {}