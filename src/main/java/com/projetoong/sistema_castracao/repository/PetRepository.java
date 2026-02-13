package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Pet;
import com.projetoong.sistema_castracao.dto.EspecieDTO; // Importante importar o seu Record
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByTutorCpf(String cpf);

    // Ajustado para usar o Record EspecieDTO em vez de Map
    @Query("SELECT new com.projetoong.sistema_castracao.dto.EspecieDTO(p.especie, COUNT(p)) " +
            "FROM Pet p GROUP BY p.especie")
    List<EspecieDTO> findEspeciesCount();

    // Mantido seu JOIN manual que funciona na sua estrutura
    @Query("SELECT COUNT(p) FROM Pet p, CadastroCastracao c WHERE c.pet.id = p.id AND c.pagamento.confirmado = true AND c.statusProcesso != 'CONCLUIDO'")
    long countByStatusFila();

    @Query("SELECT p FROM Pet p, CadastroCastracao c WHERE c.pet.id = p.id AND c.pagamento.confirmado = true AND c.statusProcesso != 'CONCLUIDO'")
    List<Pet> findPetsFilaCastracao();
}