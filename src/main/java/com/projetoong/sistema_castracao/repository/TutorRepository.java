package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    /**
     * O 'Optional' evita o erro de NullPointerException no CadastroService.
     * É a base para "puxar o histórico" pelo CPF de forma segura.
     */
    Optional<Tutor> findByCpf(String cpf);

    /**
     * Verifica se o CPF já existe para lançar um alerta no Frontend
     * antes mesmo do usuário terminar o cadastro.
     */
    boolean existsByCpf(String cpf);

    // Método para a barra de pesquisa da ONG
    List<Tutor> findByNomeContainingIgnoreCase(String nome);

    // O count que usamos no Dashboard
    long count();

    Optional<Tutor> findByEmail(String email);
}