package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.repository.AgendamentoRepository;
import com.projetoong.sistema_castracao.repository.CadastroCastracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private CadastroCastracaoRepository cadastroRepository;

    @Autowired
    private EmailService emailService; // Injeção necessária para o disparo automático

    @Transactional
    public Agendamento criarNovoAgendamento(Long cadastroId, String dataHora, String local) {
        // 1. Busca o cadastro pai (Puxa Pet e Tutor automaticamente)
        CadastroCastracao cadastro = cadastroRepository.findById(cadastroId)
                .orElseThrow(() -> new RuntimeException("Cadastro não encontrado"));

        // 2. Cria a nova entidade de agendamento e popula os dados logísticos
        Agendamento agendamento = new Agendamento();
        agendamento.setCadastro(cadastro);
        agendamento.setDataHora(LocalDateTime.parse(dataHora));
        agendamento.setLocal(local);

        // 3. Gera o código único (Hash) para segurança e validação no PDF
        agendamento.setCodigoHash(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // 4. Atualiza o status do cadastro pai para retirá-lo da fila de espera
        cadastro.setStatusProcesso("AGENDADO");
        cadastroRepository.save(cadastro);

        // 5. Persiste na tabela de agendados
        Agendamento salvo = agendamentoRepository.save(agendamento);

        // 6. DISPARO AUTOMÁTICO: Envia e-mail com Data, Hora, Local e Hash
        try {
            emailService.enviarEmailAgendamento(salvo);
        } catch (Exception e) {
            // Logamos o erro mas não travamos a transação do banco
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }

        return salvo;
    }
    @Transactional
    public Agendamento reagendar(Long agendamentoId, String novaDataHora, String novoLocal) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        // Atualiza os dados
        agendamento.setDataHora(LocalDateTime.parse(novaDataHora));
        agendamento.setLocal(novoLocal);

        // IMPORTANTE: Gera um NOVO Hash para o novo documento
        agendamento.setCodigoHash(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Agendamento atualizado = agendamentoRepository.save(agendamento);

        // Dispara o e-mail de RETIFICAÇÃO
        emailService.enviarEmailAgendamento(atualizado);

        return atualizado;
    }
    public List<Agendamento> listarAgendamentosPendentes() {
        // Aqui o Service usa o Repository para buscar os dados
        return agendamentoRepository.findByRealizadoFalseOrderByDataHoraAsc();
    }
    // No seu AgendamentoService.java
    public Agendamento buscarPorHash(String hash) {
        return agendamentoRepository.findByCodigoHash(hash)
                .orElseThrow(() -> new RuntimeException("Guia não encontrada com o hash: " + hash));
    }
}