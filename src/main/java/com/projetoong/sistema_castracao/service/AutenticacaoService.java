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

        // 2. Verifica se a conta está ativa (Importante para o controle da ONG)
        if (!admin.isAtivo()) {
            throw new RuntimeException("Sua conta está inativa. Por favor, contate a administração.");
        }

        // 3. Validação da senha com BCrypt
        // Como o ajuste já foi feito, não precisamos mais do 'save' aqui dentro
        if (!passwordEncoder.matches(dados.senha(), admin.getSenha())) {
            throw new RuntimeException("E-mail ou senha incorretos.");
        }

        // 4. Login bem-sucedido: Gera o Token JWT com as permissões (MASTER, CLINICA ou VOLUNTARIO)
        return tokenService.gerarToken(admin);
    }

}