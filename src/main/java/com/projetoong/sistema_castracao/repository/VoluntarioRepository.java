package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {

    // A chave para sua lógica de "Cadastro ou Atualização"
    Optional<Voluntario> findByCpf(String cpf);

    // 2. Se o seu campo de e-mail na Entity se chama "emailContato", use este:
    Optional<Voluntario> findByEmailContato(String emailContato);

    // --- ADICIONE ESTE MÉTODO ABAIXO ---
    // Ele permite buscar o voluntário usando o ID do Administrador logado
    // O Spring JPA entende que deve olhar para a propriedade "administrador" e pegar o "id" dela
    Optional<Voluntario> findByAdministradorId(Long administradorId);

    // Contagem para o Dashboard da ONG
    long countByAtivo(boolean ativo);
}