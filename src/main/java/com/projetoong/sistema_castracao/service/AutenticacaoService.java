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
        // 1. Busca o admin pelo email
        Administrador admin = repository.findByEmail(dados.email())
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado"));

        // --- NOVO AJUSTE: VERIFICA SE O USUÁRIO ESTÁ ATIVO ---
        if (!admin.isAtivo()) {
            throw new RuntimeException("Conta desativada. Entre em contato com a ONG.");
        }

        // 2. Compara a senha (usando BCrypt)
        if (!passwordEncoder.matches(dados.senha(), admin.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        // 3. Se deu tudo certo, gera e retorna o Token JWT
        return tokenService.gerarToken(admin);
    }
}