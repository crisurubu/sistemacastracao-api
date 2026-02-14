package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {
    // A chave para sua lógica de "Cadastro ou Atualização"
    Optional<Voluntario> findByCpf(String cpf);

    // Para validar se o e-mail já está em uso por outro voluntário
    Optional<Voluntario> findByEmailContato(String email);

    // ADICIONE ESTA LINHA:
    long countByAtivo(boolean ativo);
}