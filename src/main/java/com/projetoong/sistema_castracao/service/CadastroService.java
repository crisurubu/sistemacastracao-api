package com.projetoong.sistema_castracao.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.projetoong.sistema_castracao.dto.CadastroPetRecord;
import com.projetoong.sistema_castracao.dto.HistoricoCompletoDTO;
import com.projetoong.sistema_castracao.model.*;
import com.projetoong.sistema_castracao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime; // Import necessário para a data
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CadastroService {

    @Autowired private TutorRepository tutorRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private CadastroRepository cadastroRepository;
    @Autowired private Cloudinary cloudinary;
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private ConfiguracaoPixService pixService;

    @Transactional
    public void cadastrar(CadastroPetRecord dados, MultipartFile arquivo) {

        // 1. LÓGICA DO TUTOR (Busca por CPF ou cria novo)
        Tutor tutor = tutorRepository.findByCpf(dados.cpf())
                .map(tutorExistente -> {
                    tutorExistente.setLogradouro(dados.logradouro());
                    tutorExistente.setNumero(dados.numero());
                    tutorExistente.setBairro(dados.bairro());
                    tutorExistente.setCidade(dados.cidade() != null ? dados.cidade() : "Tatuí");
                    tutorExistente.setWhatsapp(dados.whatsapp());
                    tutorExistente.setEndereco(tutorExistente.getEnderecoCompleto());
                    return tutorRepository.save(tutorExistente);
                })
                .orElseGet(() -> {
                    Tutor novo = new Tutor();
                    novo.setNome(dados.nomeTutor());
                    novo.setCpf(dados.cpf());
                    novo.setEmail(dados.email());
                    novo.setWhatsapp(dados.whatsapp());
                    novo.setLogradouro(dados.logradouro());
                    novo.setNumero(dados.numero());
                    novo.setBairro(dados.bairro());
                    novo.setCidade(dados.cidade() != null ? dados.cidade() : "Tatuí");
                    novo.setEndereco(novo.getEnderecoCompleto());
                    return tutorRepository.save(novo);
                });

        // 2. LÓGICA DO PET
        Pet pet = new Pet();
        pet.setNomeAnimal(dados.nomePet());
        pet.setEspecie(dados.especie());
        pet.setSexo(dados.sexo());
        pet.setIdadeAprox(dados.idadeAprox());
        pet.setVacinado(dados.vacinado());
        pet.setOperouAntes(dados.operouAntes());
        pet.setMedicamentos(dados.medicamentos());
        pet.setTutor(tutor);
        pet = petRepository.save(pet);

        // 3. LÓGICA DO CADASTRO (AQUI ENTRA A DATA)
        CadastroCastracao cadastro = new CadastroCastracao();
        cadastro.setPet(pet);
        cadastro.setTutor(tutor);
        cadastro.setStatusProcesso("AGUARDANDO_PAGAMENTO");

        // Garante que a data da solicitação seja gravada agora
        if (cadastro.getDataSolicitacao() == null) {
            cadastro.setDataSolicitacao(LocalDateTime.now());
        }

        // 4. LÓGICA DO PAGAMENTO
        Pagamento pagamento = new Pagamento();
        ConfiguracaoPix configAtiva = pixService.buscarChaveAtiva();

        if (configAtiva.getValorTaxa() != null) {
            pagamento.setValorContribuicao(configAtiva.getValorTaxa().doubleValue());
        } else {
            pagamento.setValorContribuicao(25.0);
        }

        pagamento.setContaDestino(configAtiva);
        pagamento.setConfirmado(false);
        pagamento.setCadastro(cadastro);

        // 5. UPLOAD PARA CLOUDINARY
        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(arquivo.getBytes(),
                        ObjectUtils.asMap("folder", "comprovantes_ong"));
                pagamento.setComprovanteUrl(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException("Falha ao salvar comprovante na nuvem. Tente novamente.");
            }
        } else {
            pagamento.setComprovanteUrl("sem-comprovante.jpg");
        }

        // 6. SALVAMENTO FINAL EM CASCATA
        cadastro.setPagamento(pagamento);
        cadastroRepository.save(cadastro);

        System.out.println("✅ Cadastro OK! Pet: " + pet.getNomeAnimal() + " | Valor Gravado: R$ " + pagamento.getValorContribuicao());
    }

    // ... (mantenha os seus imports e o restante do service igual)

    public List<HistoricoCompletoDTO> buscarHistoricoPorTutor(Long tutorId) {
        List<CadastroCastracao> cadastros = cadastroRepository.findByTutorId(tutorId);

        return cadastros.stream().map(c -> {
            Agendamento ag = agendamentoRepository.findByCadastroId(c.getId()).orElse(null);
            Clinica cli = (ag != null) ? ag.getClinica() : null;

            String clinicaEndFormatado = "AGUARDANDO DEFINIÇÃO";
            if (cli != null) {
                clinicaEndFormatado = String.format("%s, %s - %s, %s",
                        cli.getLogradouro() != null ? cli.getLogradouro() : "",
                        cli.getNumero() != null ? cli.getNumero() : "S/N",
                        cli.getBairro() != null ? cli.getBairro() : "",
                        cli.getCidade() != null ? cli.getCidade() : "");
            }

            return new HistoricoCompletoDTO(
                    // Tutor
                    c.getTutor().getId(),
                    c.getTutor().getNome(),
                    c.getTutor().getCpf(),
                    c.getTutor().getEmail(),
                    c.getTutor().getWhatsapp(),
                    c.getTutor().getLogradouro(),
                    c.getTutor().getNumero(),
                    c.getTutor().getBairro(),
                    c.getTutor().getCidade(),
                    c.getTutor().getEnderecoCompleto(),

                    // Pet
                    c.getPet().getId(),
                    c.getPet().getNomeAnimal(),
                    c.getPet().getEspecie(),
                    c.getPet().getSexo(),
                    c.getPet().getIdadeAprox(),
                    c.getPet().isVacinado(),
                    c.getPet().isOperouAntes(),
                    c.getPet().getMedicamentos(),

                    // Cadastro
                    c.getId(),
                    c.getDataSolicitacao(),
                    c.getStatusProcesso(),

                    // Agendamento
                    (ag != null) ? ag.getId() : null,
                    (ag != null) ? ag.getDataHora() : null,
                    (ag != null) ? ag.getLocal() : "A DEFINIR",
                    (ag != null) ? ag.getCodigoHash() : "SEM HASH",
                    (ag != null) && ag.isRealizado(),
                    (ag != null) ? ag.getDataRegistro() : null,
                    (ag != null) ? ag.getAgendadorNome() : "SISTEMA",

                    // Clínica (Aqui os campos devem bater com o novo record)
                    (cli != null) ? cli.getId() : null,
                    (cli != null) ? cli.getNome() : "AGUARDANDO DEFINIÇÃO",
                    (cli != null) ? cli.getCnpj() : "---",
                    (cli != null) ? cli.getCrmvResponsavel() : "---",
                    (cli != null) ? cli.getTelefone() : "---",
                    clinicaEndFormatado,
                    (cli != null) ? cli.getEmail() : "---",
                    (cli != null) ? cli.getTotalCastracoes() : 0,
                    (cli != null) ? (cli.getSelo() != null ? cli.getSelo().toString() : "INICIANTE") : "N/A",

                    // --- O AJUSTE ESTÁ AQUI (38º CAMPO) ---
                    (cli != null) ? cli.getDataCadastro() : null
            );
        }).collect(Collectors.toList());
    }

// ... (restante do código igual)

    public List<Pet> buscarPetsPorCpf(String cpf) {
        return petRepository.findByTutorCpf(cpf);
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }
}