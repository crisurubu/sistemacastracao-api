package com.projetoong.sistema_castracao.config;

import com.projetoong.sistema_castracao.model.Administrador;
import com.projetoong.sistema_castracao.model.Role;
import com.projetoong.sistema_castracao.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private AdministradorRepository adminRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existe algum administrador para não duplicar toda vez que rodar
        if (adminRepository.count() == 0) {
            Administrador adminMaster = new Administrador();
            adminMaster.setNome("Dona da ONG");
            adminMaster.setEmail("admin@ong.com");
            // Nota: No futuro usaremos BCrypt para encriptar esta senha!
            adminMaster.setSenha("123456");
            adminMaster.setNivelAcesso(Role.MASTER);

            adminRepository.save(adminMaster);
            System.out.println("✅ Administrador Master criado com sucesso: admin@ong.com / 123456");
        } else {
            System.out.println("ℹ️ Administradores já existem no banco de dados.");
        }
    }
}