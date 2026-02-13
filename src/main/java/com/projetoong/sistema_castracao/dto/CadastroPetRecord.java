package com.projetoong.sistema_castracao.dto;

public record CadastroPetRecord(
        // Dados do Tutor
        String nomeTutor,
        String cpf,
        String email,
        String whatsapp,

        // Nova Realidade: Endereço Detalhado
        String logradouro,
        String numero,
        String bairro,
        String cidade,

        // Dados do Pet
        String nomePet,
        String especie,
        String sexo,
        String idadeAprox,
        boolean vacinado,
        boolean operouAntes,
        String medicamentos,
        // ADICIONE ESTES DOIS CAMPOS ABAIXO:
        String statusProgresso, // Para vir o "CONCLUIDO"
        String dataConcluido    // Para vir a data "10/02/2026"
) {}