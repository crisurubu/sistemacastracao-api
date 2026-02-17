package com.projetoong.sistema_castracao.repository;

import com.projetoong.sistema_castracao.model.CadastroCastracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CadastroRepository extends JpaRepository<CadastroCastracao, Long> {

    // 1. Alarme da Fila: Busca processos pelo status (Ex: "NA_FILA")
    List<CadastroCastracao> findByStatusProcesso(String statusProcesso);

    // 2. KPI Dashboard: Conta quantos pets estão aguardando
    long countByStatusProcesso(String statusProcesso);

    // 3. Histórico de Vida: Puxa todos os registros de um pet específico
    // Usamos 'petId' porque na Model CadastroCastracao o campo se chama 'pet'
    List<CadastroCastracao> findByPetId(Long petId);

    // Adicione este aqui para aceitar o ID que vem do Front-end
    List<CadastroCastracao> findByTutorId(Long tutorId);

    // 4. Busca por CPF (Puxando o histórico de vida completo do tutor)
    @Query("SELECT c FROM CadastroCastracao c WHERE c.tutor.cpf = :cpf")
    List<CadastroCastracao> findByTutorCpf(@Param("cpf") String cpf);
}