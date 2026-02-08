package com.projetoong.sistema_castracao.dto;

import com.projetoong.sistema_castracao.model.Pet;
import com.projetoong.sistema_castracao.model.Tutor;
import java.util.List;

public class TutorDTO {
    public Long id;
    public String nome;
    public String cpf;
    public String email;
    public String whatsapp;
    public String endereco;
    public String bairro;
    public String cidade;
    public List<Pet> pets; // Aqui os pets aparecem!

    public TutorDTO(Tutor tutor) {
        this.id = tutor.getId();
        this.nome = tutor.getNome();
        this.cpf = tutor.getCpf();
        this.email = tutor.getEmail();
        this.whatsapp = tutor.getWhatsapp();
        this.endereco = tutor.getEnderecoCompleto(); // Puxa o método que já criamos
        this.bairro = tutor.getBairro();
        this.cidade = tutor.getCidade();
        this.pets = tutor.getPets(); // Aqui o Hibernate carrega os pets só para esta consulta
    }
}