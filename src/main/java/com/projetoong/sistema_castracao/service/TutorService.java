package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.dto.TutorDTO;
import com.projetoong.sistema_castracao.model.Tutor;
import com.projetoong.sistema_castracao.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    // Busca para o ADMIN (via ID)
    public TutorDTO buscarPorId(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado"));

        return new TutorDTO(tutor); // Transforma em DTO antes de enviar
    }

    // Busca para o PORTAL PÚBLICO (via CPF)
    // Aqui retornamos o Tutor completo, que já carrega seus Pets automaticamente
    public Tutor buscarPorCpf(String cpf) {
        return tutorRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Nenhum histórico encontrado para o CPF: " + cpf));
    }

    // --- NOVO MÉTODO (Para o Formulário de Cadastro/Engenharia Reversa) ---
    // Este não lança erro se não achar, ele apenas retorna vazio (Optional)
    public Optional<Tutor> consultarParaCadastro(String cpfRecebido) {
        // Se o CPF veio sem máscara (11 dígitos), nós colocamos a máscara antes de buscar no banco
        String cpfComMascara = cpfRecebido;
        if (cpfRecebido.length() == 11) {
            cpfComMascara = cpfRecebido.substring(0, 3) + "." +
                    cpfRecebido.substring(3, 6) + "." +
                    cpfRecebido.substring(6, 9) + "-" +
                    cpfRecebido.substring(9, 11);
        }

        // Agora o repository vai buscar "309.682.048-13" e VAI achar!
        return tutorRepository.findByCpf(cpfComMascara);
    }
    public Optional<Tutor> buscarPorEmail(String email) {
        return tutorRepository.findByEmail(email);
    }
}