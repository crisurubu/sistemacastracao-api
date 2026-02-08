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

    // 1. Injetamos o Cloudinary que configuramos
    @Autowired private Cloudinary cloudinary;

    @Transactional
    public void cadastrar(CadastroPetRecord dados, MultipartFile arquivo) {
        // --- LOGICA DO TUTOR (PRESERVADA) ---
        Tutor tutor = tutorRepository.findByCpf(dados.cpf())
                .map(tutorExistente -> {
                    tutorExistente.setLogradouro(dados.logradouro());
                    tutorExistente.setNumero(dados.numero());
                    tutorExistente.setBairro(dados.bairro());
                    tutorExistente.setCidade(dados.cidade());
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

        // --- LOGICA DO PET (PRESERVADA) ---
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

        // --- LOGICA DO CADASTRO (PRESERVADA) ---
        CadastroCastracao cadastro = new CadastroCastracao();
        cadastro.setPet(pet);
        cadastro.setTutor(tutor);
        cadastro.setStatusProcesso("AGUARDANDO_PAGAMENTO");

        // --- AQUI ACONTECE A MUDANÇA (CLOUD UPLOAD) ---
        Pagamento pagamento = new Pagamento();
        pagamento.setValorContribuicao(20.0);
        pagamento.setConfirmado(false);
        pagamento.setCadastro(cadastro);

        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                // Sobe o arquivo para a nuvem
                Map uploadResult = cloudinary.uploader().upload(arquivo.getBytes(),
                        ObjectUtils.asMap("folder", "comprovantes_ong"));

                // SALVA A URL COMPLETA DA NUVEM (Ex: https://res.cloudinary.com/...)
                pagamento.setComprovanteUrl(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                // Se der erro no upload, salvamos um aviso ou paramos o processo
                throw new RuntimeException("Falha ao salvar comprovante na nuvem. Tente novamente.");
            }
        } else {
            pagamento.setComprovanteUrl("sem-comprovante.jpg");
        }

        cadastro.setPagamento(pagamento);
        cadastroRepository.save(cadastro);

        System.out.println("✅ Cadastro Finalizado com Cloudinary: " + pet.getNomeAnimal() + " | Bairro: " + tutor.getBairro());
    }

    // Outros métodos permanecem iguais...
    public List<Pet> buscarPetsPorCpf(String cpf) { return petRepository.findByTutorCpf(cpf); }
    public List<Pet> listarTodos() { return petRepository.findAll(); }
}