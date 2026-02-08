package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByTutorCpf(String cpf);

    @Query("SELECT p.especie as name, COUNT(p) as value FROM Pet p GROUP BY p.especie")
    List<Map<String, Object>> findEspeciesCount();

    // MANTENHA ESTE (O JOIN manual que funciona com a sua estrutura)
    @Query("SELECT COUNT(p) FROM Pet p, CadastroCastracao c WHERE c.pet.id = p.id AND c.pagamento.confirmado = true AND c.statusProcesso != 'CONCLUIDO'")
    long countByStatusFila();

    @Query("SELECT p FROM Pet p, CadastroCastracao c WHERE c.pet.id = p.id AND c.pagamento.confirmado = true AND c.statusProcesso != 'CONCLUIDO'")
    List<Pet> findPetsFilaCastracao();

    // DELETE a versão que você colou por último para parar o erro de "Method already defined"
}