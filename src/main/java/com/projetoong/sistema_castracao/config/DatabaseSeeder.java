package com.projetoong.sistema_castracao.config;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Role;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private AdministradorRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Administrador adminMaster = new Administrador();

            // Dados atualizados conforme sua solicitação
            adminMaster.setNome("Sistema Castracao ong");
            adminMaster.setEmail("sistemacastracao@gmail.com");
            adminMaster.setSenha(passwordEncoder.encode("admin123"));
            adminMaster.setNivelAcesso(Role.MASTER);

            // --- GARANTINDO QUE O MASTER NASÇA ATIVO ---
            adminMaster.setAtivo(true);

            adminRepository.save(adminMaster);
            System.out.println("✅ Administrador Master criado!");
            System.out.println("📧 E-mail: " + adminMaster.getEmail());
            System.out.println("🔑 Status: ATIVO");
        } else {
            System.out.println("ℹ️ Banco de dados já possui administradores cadastrados.");
        }
    }
}