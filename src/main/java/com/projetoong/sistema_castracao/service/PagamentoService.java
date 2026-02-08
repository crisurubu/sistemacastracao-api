package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.CadastroCastracao;
import com.projetoong.sistema_castracao.model.Pagamento;
import com.projetoong.sistema_castracao.model.Pet;
import com.projetoong.sistema_castracao.model.Tutor;
import com.projetoong.sistema_castracao.repository.CadastroCastracaoRepository;
import com.projetoong.sistema_castracao.repository.PagamentoRepository;
import com.projetoong.sistema_castracao.repository.PetRepository;
import com.projetoong.sistema_castracao.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CadastroCastracaoRepository cadastroRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Transactional
    public void confirmarPagamento(Long pagamentoId) {
        // 1. Busca o pagamento
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        // 2. Pega o Cadastro vinculado ao pagamento
        CadastroCastracao cadastro = pagamento.getCadastro();

        // 3. Puxa os dados para o e-mail de forma segura
        // Verifique se na sua classe CadastroCastracao existe o campo 'tutor' e 'pet'
        String emailTutor = cadastro.getTutor().getEmail();
        String nomePet = cadastro.getPet().getNomeAnimal();

        // 4. Atualiza o status
        pagamento.setConfirmado(true);
        pagamento.setDataConfirmacao(LocalDateTime.now());
        cadastro.setStatusProcesso("NA_FILA");

        pagamentoRepository.save(pagamento);

        // 5. Envia o e-mail
        emailService.enviarRecomendacoes(emailTutor, nomePet);
    }
    // Alarme para a Dona da ONG: Quem enviou comprovante mas não foi aprovado ainda
    public List<Pagamento> listarPendentes() {
        return pagamentoRepository.findByConfirmadoFalse();
    }

    @Transactional
    public void rejeitarERemoverTudo(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        CadastroCastracao cadastro = pagamento.getCadastro();

        // 1. Deletamos o pagamento primeiro
        pagamentoRepository.delete(pagamento);

        if (cadastro != null) {
            Tutor tutor = cadastro.getTutor();
            Pet pet = cadastro.getPet();

            // 2. Deletamos o vínculo do cadastro (O processo de castração desse pet)
            cadastroRepository.delete(cadastro);

            // 3. Deletamos o Pet que teve o erro no pagamento
            if (pet != null) {
                petRepository.delete(pet);
            }

            // 4. VERIFICAÇÃO DE SEGURANÇA PARA O TUTOR:
            // Buscamos se esse tutor tem outros cadastros no sistema
            List<CadastroCastracao> outrosCadastros = cadastroRepository.findByTutor(tutor);

            if (outrosCadastros.isEmpty()) {
                // Se ele não tem mais nenhum animal, podemos apagar o tutor
                tutorRepository.delete(tutor);
                System.out.println("Tutor removido: era o único cadastro.");
            } else {
                // Se ele tem outros pets aprovados, mantemos o tutor vivo no banco!
                System.out.println("Tutor preservado: ele possui outros pets no sistema.");
            }
        }
    }

}