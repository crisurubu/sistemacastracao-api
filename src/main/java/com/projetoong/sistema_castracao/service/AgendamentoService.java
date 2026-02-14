package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Agendamento;
import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.model.Voluntario;
import com.projetoong.sistema_castracao.repository.AgendamentoRepository;
import com.projetoong.sistema_castracao.repository.CadastroCastracaoRepository;
import com.projetoong.sistema_castracao.repository.ClinicaRepository;
import com.projetoong.sistema_castracao.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private VoluntarioRepository voluntarioRepository;
    @Autowired
    private EmailService emailService;

    public List<Agendamento> listarAgendaDaClinica(Clinica clinica) {
        return agendamentoRepository.findByClinicaAndRealizadoFalseOrderByDataHoraAsc(clinica);
    }

    @Transactional
    public void concluirProcedimento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        if (agendamento.isRealizado()) throw new RuntimeException("Este procedimento já foi marcado como realizado.");

        agendamento.setRealizado(true);
        agendamentoRepository.save(agendamento);

        CadastroCastracao cadastro = agendamento.getCadastro();
        cadastro.setStatusProcesso("CONCLUIDO");
        cadastroRepository.save(cadastro);

        clinicaService.registrarCastracaoConcluida(agendamento.getClinica().getId());
    }

    @Transactional
    public Agendamento criarNovoAgendamento(Long cadastroId, String dataHora, Long clinicaId, String emailLogado) {
        CadastroCastracao cadastro = cadastroRepository.findById(cadastroId)
                .orElseThrow(() -> new RuntimeException("Cadastro não encontrado"));
        Clinica clinica = clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        Agendamento agendamento = new Agendamento();
        agendamento.setCadastro(cadastro);
        agendamento.setClinica(clinica);
        agendamento.setDataHora(LocalDateTime.parse(dataHora));
        agendamento.setLocal(clinica.getNome());
        agendamento.setCodigoHash(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Busca o voluntário pelo e-mail (String) extraída no Controller
        Optional<Voluntario> voluntarioOpt = voluntarioRepository.findByEmailContato(emailLogado);

        if (voluntarioOpt.isPresent()) {
            Voluntario v = voluntarioOpt.get();
            agendamento.setAgendadoPor(v);
            agendamento.setAgendadorNome(v.getNome());
        } else {
            agendamento.setAgendadoPor(null);
            if (emailLogado != null && emailLogado.equalsIgnoreCase("sistemacastracao@gmail.com")) {
                agendamento.setAgendadorNome("Sistema Castracao ong");
            } else {
                agendamento.setAgendadorNome("SISTEMA MASTER (ONG)");
            }
        }

        cadastro.setStatusProcesso("AGENDADO");
        cadastroRepository.save(cadastro);
        Agendamento salvo = agendamentoRepository.save(agendamento);

        try { emailService.enviarEmailAgendamento(salvo); }
        catch (Exception e) { System.err.println("Erro e-mail: " + e.getMessage()); }

        return salvo;
    }

    @Transactional
    public Agendamento reagendar(Long agendamentoId, String novaDataHora, Long novaClinicaId, String emailLogado) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        Clinica novaClinica = clinicaRepository.findById(novaClinicaId)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        agendamento.setDataHora(LocalDateTime.parse(novaDataHora));
        agendamento.setClinica(novaClinica);
        agendamento.setLocal(novaClinica.getNome());
        agendamento.setCodigoHash(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Busca o voluntário pelo e-mail (String) para manter padrão com o Controller
        Optional<Voluntario> voluntarioOpt = voluntarioRepository.findByEmailContato(emailLogado);

        if (voluntarioOpt.isPresent()) {
            Voluntario v = voluntarioOpt.get();
            agendamento.setAgendadoPor(v);
            agendamento.setAgendadorNome(v.getNome());
        } else {
            agendamento.setAgendadoPor(null);
            if (emailLogado != null && emailLogado.equalsIgnoreCase("sistemacastracao@gmail.com")) {
                agendamento.setAgendadorNome("Sistema Castracao ong");
            } else {
                agendamento.setAgendadorNome("Sistema Master (ONG)");
            }
        }

        Agendamento atualizado = agendamentoRepository.save(agendamento);

        try { emailService.enviarEmailAgendamento(atualizado); }
        catch (Exception e) { System.err.println("Erro e-mail reagendamento: " + e.getMessage()); }

        return atualizado;
    }

    public List<Agendamento> listarAgendamentosPendentes() {
        return agendamentoRepository.findByRealizadoFalseOrderByDataHoraAsc();
    }

    public Agendamento buscarPorHash(String hash) {
        return agendamentoRepository.findByCodigoHash(hash)
                .orElseThrow(() -> new RuntimeException("Guia não encontrada: " + hash));
    }
}