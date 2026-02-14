package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Clinica;
import com.projetoong.sistema_castracao.model.Role;
import com.projetoong.sistema_castracao.model.SeloParceiro;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.repository.AgendamentoRepository;
import com.projetoong.sistema_castracao.repository.ClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClinicaService {

    @Autowired
    private ClinicaRepository clinicaRepository;
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    // --- 1. SALVAR OU ATUALIZAR ---
    @Transactional
    public Clinica salvarOuAtualizar(Clinica clinica) {
        return clinicaRepository.findByCnpj(clinica.getCnpj())
                .map(existente -> atualizarExistente(existente, clinica))
                .orElseGet(() -> cadastrarNova(clinica));
    }

    private Clinica cadastrarNova(Clinica clinica) {
        Administrador adm = clinica.getAdministrador();
        String senhaOriginal = adm.getSenha();

        clinica.setEmail(adm.getEmail());
        adm.setSenha(passwordEncoder.encode(senhaOriginal));
        adm.setNivelAcesso(Role.CLINICA);
        adm.setAtivo(true);

        clinica.setAdministrador(administradorRepository.save(adm));
        Clinica salva = clinicaRepository.save(clinica);

        try {
            emailService.enviarEmailBoasVindasClinica(salva, senhaOriginal);
        } catch (Exception e) {
            System.err.println("Erro e-mail boas-vindas: " + e.getMessage());
        }
        return salva;
    }

    // --- 2. ATUALIZAR (Lógica revisada e sem logs) ---
    @Transactional
    public Clinica atualizarExistente(Clinica existente, Clinica dadosNovos) {
        existente.setNome(dadosNovos.getNome());
        existente.setTelefone(dadosNovos.getTelefone());
        existente.setEndereco(dadosNovos.getEndereco());
        existente.setCrmvResponsavel(dadosNovos.getCrmvResponsavel());

        if (dadosNovos.getAdministrador() != null) {
            Administrador adm = existente.getAdministrador();
            adm.setEmail(dadosNovos.getAdministrador().getEmail());
            existente.setEmail(adm.getEmail());

            String novaSenha = dadosNovos.getAdministrador().getSenha();
            if (novaSenha != null && !novaSenha.isBlank()) {
                adm.setSenha(passwordEncoder.encode(novaSenha));
                try {
                    emailService.enviarEmailSenhaAlteradaClinica(existente, novaSenha);
                } catch (Exception e) {
                    System.err.println("Erro e-mail alteração senha: " + e.getMessage());
                }
            }
            administradorRepository.save(adm);
        }

        Clinica salva = clinicaRepository.save(existente);

        // Garante a escrita imediata para evitar problemas de cache no login
        clinicaRepository.flush();
        administradorRepository.flush();

        return salva;
    }

    // --- 3. GESTÃO DE CASTRAÇÕES E SELOS ---
    @Transactional
    public void registrarCastracaoConcluida(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));

        int novoTotal = clinica.getTotalCastracoes() + 1;
        clinica.setTotalCastracoes(novoTotal);
        clinica.setSelo(SeloParceiro.calcular(novoTotal));

        clinicaRepository.save(clinica);
    }

    // --- 4. DASHBOARD E APOIO ---
    public Optional<Clinica> buscarPorCnpj(String cnpj) {
        return clinicaRepository.findByCnpj(cnpj);
    }

    public List<Clinica> listarTodas() {
        return clinicaRepository.findAllByOrderByTotalCastracoesDesc();
    }

    @Transactional
    public void alternarStatus(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica não encontrada"));
        clinica.getAdministrador().setAtivo(!clinica.getAdministrador().isAtivo());
    }

    public Map<String, Object> obterDadosDashboard(String email) {
        Clinica clinica = buscarPorEmail(email);
        long totalVidas = agendamentoRepository.countByClinicaAndRealizadoTrue(clinica);

        Map<String, Object> dados = new HashMap<>();
        dados.put("nomeClinica", clinica.getNome());
        dados.put("totalVidas", totalVidas);
        dados.put("selo", SeloParceiro.calcular((int) totalVidas).toString());
        dados.put("agendamentos", agendamentoRepository.findByClinicaAndRealizadoFalseOrderByDataHoraAsc(clinica));
        return dados;
    }

    public Clinica buscarPorEmail(String email) {
        Administrador adm = administradorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return clinicaRepository.findByAdministrador(adm)
                .orElseThrow(() -> new RuntimeException("Clínica não vinculada"));
    }

    public void alterarSenha(String email, String senhaAtual, String novaSenha) {
        Administrador admin = administradorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Conta de acesso não encontrada."));

        if (!passwordEncoder.matches(senhaAtual, admin.getSenha())) {
            throw new IllegalArgumentException("A senha atual digitada está incorreta.");
        }

        admin.setSenha(passwordEncoder.encode(novaSenha));
        administradorRepository.save(admin);
    }
}