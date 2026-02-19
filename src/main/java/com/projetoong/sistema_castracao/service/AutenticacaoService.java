package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.dto.LoginDTO;
import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    @Autowired
    private AdministradorRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String validarLogin(LoginDTO dados) {
        // 1. Busca o administrador pelo e-mail
        Administrador admin = repository.findByEmail(dados.email())
                .orElseThrow(() -> new RuntimeException("E-mail ou senha incorretos."));

        // 2. Validação da senha com BCrypt (MUDOU PARA CÁ!)
        // Primeiro conferimos se a senha bate. Se errar aqui, cai no erro comum.
        if (!passwordEncoder.matches(dados.senha(), admin.getSenha())) {
            throw new RuntimeException("E-mail ou senha incorretos.");
        }

        // 3. Verifica se a conta está ativa (MUDOU PARA DEPOIS DA SENHA)
        // Se a senha está certa, mas o booleano 'ativo' é false, aí sim avisamos do bloqueio.
        if (!admin.isAtivo()) {
            throw new RuntimeException("Sua conta está inativa. Por favor, contate a administração.");
        }

        // 4. Login bem-sucedido
        return tokenService.gerarToken(admin);
    }

}