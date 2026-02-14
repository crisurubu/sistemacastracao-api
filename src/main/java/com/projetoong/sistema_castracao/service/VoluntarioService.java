package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Voluntario;
import com.projetoong.sistema_castracao.model.Role;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Voluntario salvarOuAtualizar(Voluntario voluntario) {
        // 1. Busca pelo CPF para ver se já é cadastrado
        Optional<Voluntario> voluntarioExistente = voluntarioRepository.findByCpf(voluntario.getCpf());

        if (voluntarioExistente.isPresent()) {
            // --- LÓGICA DE ATUALIZAÇÃO ---
            return atualizarExistente(voluntarioExistente.get(), voluntario);
        } else {
            // --- LÓGICA DE NOVO CADASTRO ---
            return cadastrarNovo(voluntario);
        }
    }

    private Voluntario cadastrarNovo(Voluntario voluntario) {
        Administrador adm = voluntario.getAdministrador();
        String senhaOriginal = adm.getSenha();

        voluntario.setEmailContato(adm.getEmail());

        adm.setSenha(passwordEncoder.encode(senhaOriginal));
        adm.setNivelAcesso(Role.VOLUNTARIO);
        adm.setAtivo(true);

        Administrador admSalvo = administradorRepository.save(adm);
        voluntario.setAdministrador(admSalvo);

        Voluntario voluntarioSalvo = voluntarioRepository.save(voluntario);

        // Disparo de E-mail de Boas-Vindas
        try {
            emailService.enviarEmailBoasVindasVoluntario(voluntarioSalvo, senhaOriginal);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de boas-vindas: " + e.getMessage());
        }

        return voluntarioSalvo;
    }

    private Voluntario atualizarExistente(Voluntario existente, Voluntario dadosNovos) {
        // Atualiza dados básicos
        existente.setNome(dadosNovos.getNome());
        existente.setWhatsapp(dadosNovos.getWhatsapp());
        existente.setEmailContato(dadosNovos.getAdministrador().getEmail());

        // Atualiza Endereço
        existente.setCep(dadosNovos.getCep());
        existente.setLogradouro(dadosNovos.getLogradouro());
        existente.setNumero(dadosNovos.getNumero());
        existente.setBairro(dadosNovos.getBairro());
        existente.setCidade(dadosNovos.getCidade());
        existente.setEstado(dadosNovos.getEstado());

        // Atualiza o Administrador vinculado (E-mail e Nome)
        Administrador adm = existente.getAdministrador();
        adm.setNome(dadosNovos.getNome());
        adm.setEmail(dadosNovos.getAdministrador().getEmail());

        // --- AJUSTE: Se enviou nova senha, atualiza e envia e-mail de ALTERAÇÃO ---
        if (dadosNovos.getAdministrador().getSenha() != null && !dadosNovos.getAdministrador().getSenha().isEmpty()) {
            String novaSenhaPlana = dadosNovos.getAdministrador().getSenha();
            adm.setSenha(passwordEncoder.encode(novaSenhaPlana));

            try {
                // Agora chama o método específico de senha alterada em vez do de boas-vindas
                emailService.enviarEmailSenhaAlterada(existente, novaSenhaPlana);
            } catch (Exception e) {
                System.err.println("Erro ao enviar e-mail de alteração de senha: " + e.getMessage());
            }
        }

        return voluntarioRepository.save(existente);
    }

    @Transactional
    public void alternarStatus(Long id) {
        Voluntario voluntario = voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntário não encontrado"));

        boolean novoStatus = !voluntario.isAtivo();
        voluntario.setAtivo(novoStatus);

        if (voluntario.getAdministrador() != null) {
            voluntario.getAdministrador().setAtivo(novoStatus);
        }

        voluntarioRepository.save(voluntario);
    }

    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    public boolean existePorCpf(String cpf) {
        return voluntarioRepository.findByCpf(cpf).isPresent();
    }

    public Optional<Voluntario> buscarPorCpf(String cpf) {
        return voluntarioRepository.findByCpf(cpf);
    }

    public Optional<Voluntario> buscarPorId(Long id) {
        return voluntarioRepository.findById(id);
    }

    public long contarAtivos(boolean status) {
        return voluntarioRepository.countByAtivo(status);
    }
}