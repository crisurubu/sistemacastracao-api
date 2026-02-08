package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.Pet;
import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.repository.PetRepository;
import com.projetoong.sistema_castracao.repository.CadastroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CadastroRepository cadastroRepository;

    /**
     * CORREÇÃO: Agora buscamos na tabela de CADASTRO quem já pagou.
     * Não usamos mais petRepository.findByPixConfirmadoTrue() porque esse campo saiu do Pet.
     */
    public List<Pet> listarConfirmados() {
        // Buscamos os cadastros que estão na fila (pagamento confirmado)
        List<CadastroCastracao> cadastrosNaFila = cadastroRepository.findByStatusProcesso("NA_FILA");

        // Extraímos apenas os Pets desses cadastros
        return cadastrosNaFila.stream()
                .map(CadastroCastracao::getPet)
                .collect(Collectors.toList());
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    /**
     * Histórico de Vida: Busca todos os pets do tutor pelo CPF.
     */
    public List<Pet> buscarPorTutor(String cpf) {
        return petRepository.findByTutorCpf(cpf);
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com o ID: " + id));
    }
}