package com.projetoong.sistema_castracao.config;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.ConfiguracaoPix;
import com.projetoong.sistema_castracao.model.Role;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import com.projetoong.sistema_castracao.repository.ConfiguracaoPixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private AdministradorRepository adminRepository;

    @Autowired
    private ConfiguracaoPixRepository pixRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. SEEDER DO ADMINISTRADOR
        if (adminRepository.count() == 0) {
            Administrador adminMaster = new Administrador();
            adminMaster.setNome("Sistema Castracao ong");
            adminMaster.setEmail("sistemacastracao@gmail.com");
            adminMaster.setSenha(passwordEncoder.encode("admin123"));
            adminMaster.setNivelAcesso(Role.MASTER);
            adminMaster.setAtivo(true);

            adminRepository.save(adminMaster);
            System.out.println("✅ Administrador Master criado!");
        }

        // 2. SEEDER DA CONFIGURAÇÃO PIX COMPLETA
        if (pixRepository.count() == 0) {
            ConfiguracaoPix configInicial = new ConfiguracaoPix();

            // Dados oficiais da ONG
            configInicial.setChave("sistemacastracao@gmail.com");
            configInicial.setTipoChave("E-MAIL");
            configInicial.setNomeRecebedor("Sistema Castracao ong");
            configInicial.setDocumentoRecebedor("00.000.000/0001-00"); // Substitua pelo CNPJ real se tiver

            // Dados Bancários (Padronizando como Nubank do print)
            configInicial.setBanco("Nubank");
            configInicial.setAgencia("0001"); // Padrão Nubank
            configInicial.setConta("1234567-8"); // Exemplo de conta

            // Valor da Taxa atualizado conforme o banco
            configInicial.setValorTaxa(new BigDecimal("25.00"));

            configInicial.setAtivo(true);
            configInicial.setDataCriacao(LocalDateTime.now());

            pixRepository.save(configInicial);
            System.out.println("✅ Configuração PIX inicializada (Banco: Nubank, Ag: 0001, Conta: 1234567-8)");
            System.out.println("✅ Valor da taxa: R$ 25,00");
        }
    }
}