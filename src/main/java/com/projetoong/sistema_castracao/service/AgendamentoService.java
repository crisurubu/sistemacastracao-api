package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.repository.AgendamentoRepository;
import com.projetoong.sistema_castracao.repository.CadastroCastracaoRepository;
import com.projetoong.sistema_castracao.repository.ClinicaRepository;
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
    private ClinicaRepository clinicaRepository;

    @Autowired
    private ClinicaService clinicaService;

    @Autowired
    private EmailService emailService;

    // 1. Listar agenda da clínica
    public List<Agendamento> listarAgendaDaClinica(Clinica clinica) {
        return agendamentoRepository.findByClinicaAndRealizadoFalseOrderByDataHoraAsc(clinica);
    }

    // 2. Concluir o procedimento (Dá mérito à clínica)
    @Transactional
    public void concluirProcedimento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (agendamento.isRealizado()) {
            throw new RuntimeException("Este procedimento já foi marcado como realizado.");
        }

        if (agendamento.getClinica() == null) {
            throw new RuntimeException("Este agendamento não possui uma clínica vinculada.");
        }

        agendamento.setRealizado(true);
        agendamentoRepository.save(agendamento);

        CadastroCastracao cadastro = agendamento.getCadastro();
        cadastro.setStatusProcesso("CONCLUIDO");
        cadastroRepository.save(cadastro);

        clinicaService.registrarCastracaoConcluida(agendamento.getClinica().getId());
    }

    // 3. Criar agendamento (Vincula a clínica pelo ID)
    @Transactional
    public Agendamento criarNovoAgendamento(Long cadastroId, String dataHora, Long clinicaId) {
        CadastroCastracao cadastro = cadastroRepository.findById(cadastroId)
                .orElseThrow(() -> new RuntimeException("Cadastro não encontrado"));

        Clinica clinica = clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        Agendamento agendamento = new Agendamento();
        agendamento.setCadastro(cadastro);
        agendamento.setDataHora(LocalDateTime.parse(dataHora));
        agendamento.setClinica(clinica);
        agendamento.setLocal(clinica.getNome());
        agendamento.setCodigoHash(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        cadastro.setStatusProcesso("AGENDADO");
        cadastroRepository.save(cadastro);

        Agendamento salvo = agendamentoRepository.save(agendamento);

        try {
            emailService.enviarEmailAgendamento(salvo);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }

        return salvo;
    }

    // 4. Reagendar (Agora dentro da classe corretamente)
    @Transactional
    public Agendamento reagendar(Long agendamentoId, String novaDataHora, Long novaClinicaId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        Clinica novaClinica = clinicaRepository.findById(novaClinicaId)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        agendamento.setDataHora(LocalDateTime.parse(novaDataHora));
        agendamento.setClinica(novaClinica);
        agendamento.setLocal(novaClinica.getNome());
        agendamento.setCodigoHash(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Agendamento atualizado = agendamentoRepository.save(agendamento);

        try {
            emailService.enviarEmailAgendamento(atualizado);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }

        return atualizado;
    }

    // 5. Listar todos os pendentes
    public List<Agendamento> listarAgendamentosPendentes() {
        return agendamentoRepository.findByRealizadoFalseOrderByDataHoraAsc();
    }
    // --- ADICIONE ESTE MÉTODO QUE ESTÁ FALTANDO ---
    public Agendamento buscarPorHash(String hash) {
        return agendamentoRepository.findByCodigoHash(hash)
                .orElseThrow(() -> new RuntimeException("Guia não encontrada com o hash: " + hash));
    }

} // FIM DA CLASSE