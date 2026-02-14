package com.projetoong.sistema_castracao.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.projetoong.sistema_castracao.dto.CadastroPetRecord;
import com.projetoong.sistema_castracao.model.*;
import com.projetoong.sistema_castracao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CadastroService {

    @Autowired private TutorRepository tutorRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private CadastroRepository cadastroRepository;
    @Autowired private Cloudinary cloudinary;

    // NOVO: Necessário para buscar o valor da taxa e a conta destino ativa
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

        // 3. LÓGICA DO CADASTRO
        CadastroCastracao cadastro = new CadastroCastracao();
        cadastro.setPet(pet);
        cadastro.setTutor(tutor);
        cadastro.setStatusProcesso("AGUARDANDO_PAGAMENTO");

        // 4. LÓGICA DO PAGAMENTO (FIM DOS NULLS E DO VALOR 20 FIXO)
        Pagamento pagamento = new Pagamento();

        // Busca a configuração que está marcada como ativa no banco (Lugar certo do alarme)
        ConfiguracaoPix configAtiva = pixService.buscarChaveAtiva();

        // Define o valor dinamicamente (BigDecimal da config -> Double do pagamento)
        if (configAtiva.getValorTaxa() != null) {
            pagamento.setValorContribuicao(configAtiva.getValorTaxa().doubleValue());
        } else {
            pagamento.setValorContribuicao(25.0); // Fallback caso a config esteja sem valor
        }

        // VINCULA A CONTA DESTINO: Mata o erro de pix_config_id = null
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

    public List<CadastroCastracao> buscarHistoricoPorTutor(Long tutorId) {
        return cadastroRepository.findByTutorId(tutorId);
    }

    public List<Pet> buscarPetsPorCpf(String cpf) {
        return petRepository.findByTutorCpf(cpf);
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }
}