package com.projetoong.sistema_castracao.service;

import com.projetoong.sistema_castracao.model.*;
import com.projetoong.sistema_castracao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private ConfiguracaoPixService pixService;

    @Transactional
    public void confirmarPagamento(Long pagamentoId, Voluntario voluntarioLogado, String emailMaster) {
        // 1. Busca o pagamento
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        // 2. BUSCA A CONTA DESTINO ATIVA AGORA
        ConfiguracaoPix pixAtivo = pixService.buscarChaveAtiva();

        // 3. Pega o Cadastro vinculado
        CadastroCastracao cadastro = pagamento.getCadastro();
        String emailTutor = cadastro.getTutor().getEmail();
        String nomePet = cadastro.getPet().getNomeAnimal();

        // 4. ATUALIZAÇÃO COM AUDITORIA
        pagamento.setConfirmado(true);
        pagamento.setDataConfirmacao(LocalDateTime.now());
        pagamento.setContaDestino(pixAtivo);

        // --- LÓGICA DE AUDITORIA CORRIGIDA ---
        Voluntario aprovadorReal = voluntarioLogado;

        // Se o objeto voluntário não veio pronto, tentamos buscar pelo e-mail que está logado
        if (aprovadorReal == null && emailMaster != null) {
            aprovadorReal = voluntarioRepository.findByEmailContato(emailMaster.trim().toLowerCase()).orElse(null);
        }

        if (aprovadorReal != null) {
            // SE IDENTIFICOU O VOLUNTÁRIO, GRAVA O ID (FK) E O NOME
            pagamento.setAprovadoPor(aprovadorReal);
            pagamento.setAprovadorNome(aprovadorReal.getNome());
        } else {
            // SE NÃO ACHOU VOLUNTÁRIO, VERIFICA SE É A ONG OU MASTER GENÉRICO
            pagamento.setAprovadoPor(null);
            if (emailMaster != null && emailMaster.equalsIgnoreCase("sistemacastracao@gmail.com")) {
                pagamento.setAprovadorNome("Sistema Castracao ong");
            } else {
                pagamento.setAprovadorNome("SISTEMA MASTER (ONG)");
            }
        }

        // --- LÓGICA DE VALOR ---
        if (pixAtivo.getValorTaxa() != null) {
            Double valorConfigurado = pixAtivo.getValorTaxa().doubleValue();
            Double valorNoBanco = pagamento.getValorContribuicao();

            if (valorNoBanco == null || valorNoBanco < valorConfigurado) {
                pagamento.setValorContribuicao(valorConfigurado);
            }
        }

        // 5. ATUALIZA O STATUS DO PROCESSO
        cadastro.setStatusProcesso("NA_FILA");

        // 6. SALVA TUDO
        pagamentoRepository.save(pagamento);

        // 7. ENVIO DO E-MAIL
        emailService.enviarRecomendacoes(emailTutor, nomePet);

        System.out.println("✅ Pagamento aprovado por: " + pagamento.getAprovadorNome() + " para: " + emailTutor);
    }

    public List<Pagamento> listarExtratoAuditoria() {
        return pagamentoRepository.findByConfirmadoTrueOrderByDataConfirmacaoDesc();
    }

    public List<Pagamento> listarPendentes() {
        return pagamentoRepository.findByConfirmadoFalse();
    }

    @Transactional
    public void rejeitarERemoverTudo(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        CadastroCastracao cadastro = pagamento.getCadastro();

        if (cadastro != null && cadastro.getTutor() != null) {
            String motivoPadrao = "Comprovante ilegível ou dados divergentes.";
            emailService.enviarEmailPagamentoNaoIdentificado(
                    cadastro.getTutor().getEmail(),
                    cadastro.getTutor().getNome(),
                    cadastro.getPet().getNomeAnimal(),
                    motivoPadrao
            );
        }

        pagamentoRepository.delete(pagamento);

        if (cadastro != null) {
            Tutor tutor = cadastro.getTutor();
            Pet pet = cadastro.getPet();

            cadastroRepository.delete(cadastro);
            if (pet != null) petRepository.delete(pet);

            List<CadastroCastracao> outrosCadastros = cadastroRepository.findByTutor(tutor);
            if (outrosCadastros.isEmpty()) {
                tutorRepository.delete(tutor);
            }
        }
    }
}