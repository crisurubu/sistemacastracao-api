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

import java.time.LocalDateTime;
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

    // --- 1. SALVAR OU ATUALIZAR (O Coração do Formulário Inteligente) ---
    @Transactional
    public Clinica salvarOuAtualizar(Clinica clinica) {
        // Busca por CNPJ para decidir se atualiza ou cadastra novo
        return clinicaRepository.findByCnpj(clinica.getCnpj())
                .map(existente -> atualizarExistente(existente, clinica))
                .orElseGet(() -> cadastrarNova(clinica));
    }

    private Clinica cadastrarNova(Clinica clinica) {
        Administrador adm = clinica.getAdministrador();
        String senhaOriginal = adm.getSenha();

        // Garante que a data de cadastro seja a atual no momento da criação
        if (clinica.getDataCadastro() == null) {
            clinica.setDataCadastro(LocalDateTime.now());
        }

        clinica.setEmail(adm.getEmail());
        adm.setSenha(passwordEncoder.encode(senhaOriginal));
        adm.setNivelAcesso(Role.CLINICA);
        adm.setAtivo(true);

        // Salva o administrador e vincula à clínica
        clinica.setAdministrador(administradorRepository.save(adm));

        // Salva a clínica com todos os novos campos de endereço (CEP, Bairro, etc)
        Clinica salva = clinicaRepository.save(clinica);

        try {
            emailService.enviarEmailBoasVindasClinica(salva, senhaOriginal);
        } catch (Exception e) {
            System.err.println("Erro e-mail boas-vindas: " + e.getMessage());
        }
        return salva;
    }

    // --- 2. ATUALIZAR (Garantindo os Alarmes por Região) ---
    public Clinica atualizarExistente(Clinica existente, Clinica dadosNovos) {
        existente.setNome(dadosNovos.getNome());
        existente.setCrmvResponsavel(dadosNovos.getCrmvResponsavel());
        existente.setTelefone(dadosNovos.getTelefone());
        existente.setEmail(dadosNovos.getEmail());

        // MAPEAR OS NOVOS CAMPOS ESTRUTURADOS (Fim do erro de campo nulo)
        existente.setCep(dadosNovos.getCep());
        existente.setLogradouro(dadosNovos.getLogradouro());
        existente.setNumero(dadosNovos.getNumero());
        existente.setBairro(dadosNovos.getBairro());
        existente.setCidade(dadosNovos.getCidade());
        existente.setEstado(dadosNovos.getEstado());

        // Mantemos a dataCadastro original do objeto 'existente' intacta

        return clinicaRepository.save(existente);
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

    // --- 4. DASHBOARD E APOIO (Ajustado para o Controller) ---

    // Adicionado para suportar o @PutMapping("/{id}") do Controller
    public Optional<Clinica> buscarPorId(Long id) {
        return clinicaRepository.findById(id);
    }

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