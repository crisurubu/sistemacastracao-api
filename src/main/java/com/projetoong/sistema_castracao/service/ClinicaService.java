package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.model.Role;
import com.projetoong.sistema_castracao.model.Agendamento; // Import novo
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.repository.ClinicaRepository;
import com.projetoong.sistema_castracao.repository.AgendamentoRepository; // Import novo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashMap; // Import novo
import java.util.Map; // Import novo

@Service
public class ClinicaService {

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository; // Adicionado para o Dashboard

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Clinica salvar(Clinica clinica) {
        Administrador adm = clinica.getAdministrador();
        String senhaOriginal = adm.getSenha();

        // Sincroniza o e-mail: O e-mail da Clínica deve ser o mesmo do Administrador
        clinica.setEmail(adm.getEmail());

        adm.setSenha(passwordEncoder.encode(senhaOriginal));
        adm.setNivelAcesso(Role.CLINICA);
        adm.setAtivo(true);

        Administrador admSalvo = administradorRepository.save(adm);
        clinica.setAdministrador(admSalvo);

        Clinica clinicaSalva = clinicaRepository.save(clinica);

        try {
            emailService.enviarEmailBoasVindasClinica(clinicaSalva, senhaOriginal);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail para a clínica: " + e.getMessage());
        }

        return clinicaSalva;
    }

    // --- Busca otimizada: Tenta direto pelo e-mail da clínica ou via Administrador ---
    public Clinica buscarPorEmail(String email) {
        System.out.println("--- INÍCIO DA BUSCA NO SERVICE ---");
        System.out.println("Buscando e-mail: [" + email + "]");

        // 1. Tenta achar o Administrador
        Administrador adm = administradorRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("ERRO: E-mail não existe na tabela administradores.");
                    return new RuntimeException("Usuário não cadastrado no sistema.");
                });

        System.out.println("ADM encontrado! ID: " + adm.getId());

        // 2. Tenta achar a Clínica ligada a esse ADM
        return clinicaRepository.findByAdministrador(adm)
                .orElseThrow(() -> {
                    System.out.println("ERRO: Nenhuma clínica aponta para o ADM ID: " + adm.getId());
                    return new RuntimeException("Sua conta não tem uma clínica vinculada.");
                });
    }

    public List<Clinica> listarTodas() {
        return clinicaRepository.findAllByOrderByTotalCastracoesDesc();
    }

    public boolean existePorCnpj(String cnpj) {
        return clinicaRepository.findByCnpj(cnpj).isPresent();
    }

    @Transactional
    public void registrarCastracaoConcluida(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        clinica.setTotalCastracoes(clinica.getTotalCastracoes() + 1);
        clinicaRepository.save(clinica);
    }

    @Transactional
    public Clinica atualizar(Long id, Clinica dadosNovos) {
        Clinica clinicaExistente = clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        clinicaExistente.setNome(dadosNovos.getNome());
        clinicaExistente.setTelefone(dadosNovos.getTelefone());
        clinicaExistente.setEndereco(dadosNovos.getEndereco());
        clinicaExistente.setCrmvResponsavel(dadosNovos.getCrmvResponsavel());

        // Mantém o e-mail sincronizado na atualização
        if (dadosNovos.getAdministrador() != null) {
            String novoEmail = dadosNovos.getAdministrador().getEmail();
            clinicaExistente.setEmail(novoEmail);

            Administrador adm = clinicaExistente.getAdministrador();
            adm.setNome(dadosNovos.getNome());
            adm.setEmail(novoEmail);

            if (dadosNovos.getAdministrador().getSenha() != null && !dadosNovos.getAdministrador().getSenha().isEmpty()) {
                adm.setSenha(passwordEncoder.encode(dadosNovos.getAdministrador().getSenha()));
            }
        }

        return clinicaRepository.save(clinicaExistente);
    }

    @Transactional
    public void alternarStatus(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        Administrador adm = clinica.getAdministrador();
        adm.setAtivo(!adm.isAtivo());

        administradorRepository.save(adm);
    }

    // --- NOVO MÉTODO PARA O DASHBOARD (Contador de Vidas) ---
    public Map<String, Object> obterDadosDashboard(String email) {
        Clinica clinica = buscarPorEmail(email);

        // 1. Pega os agendamentos que ainda NÃO foram realizados (Fila da tabela)
        List<Agendamento> pendentes = agendamentoRepository.findByClinicaAndRealizadoFalseOrderByDataHoraAsc(clinica);

        // 2. CONTA O TOTAL DE TRUE (Histórico de Vidas Salvas)
        long totalVidas = agendamentoRepository.countByClinicaAndRealizadoTrue(clinica);

        Map<String, Object> dados = new HashMap<>();
        dados.put("nomeClinica", clinica.getNome());
        dados.put("agendamentos", pendentes);
        dados.put("totalVidas", totalVidas); // Esse campo atualiza o 0 no React

        // Lógica de Selo
        String selo = "BRONZE";
        if (totalVidas >= 50) selo = "OURO";
        else if (totalVidas >= 20) selo = "PRATA";
        dados.put("selo", selo);

        return dados;
    }
}