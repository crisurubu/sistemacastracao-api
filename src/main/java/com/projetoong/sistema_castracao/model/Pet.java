package com.projetoong.sistema_castracao.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animais")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAnimal;
    private String especie;
    private String sexo;
    private String idadeAprox;
    private boolean vacinado;
    private boolean operouAntes;
    private String medicamentos;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @JsonBackReference
    private Tutor tutor;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CadastroCastracao> historicoVida = new ArrayList<>();

    public Pet() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeAnimal() { return nomeAnimal; }
    public void setNomeAnimal(String nomeAnimal) { this.nomeAnimal = nomeAnimal; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getIdadeAprox() { return idadeAprox; }
    public void setIdadeAprox(String idadeAprox) { this.idadeAprox = idadeAprox; }
    public boolean isVacinado() { return vacinado; }
    public void setVacinado(boolean vacinado) { this.vacinado = vacinado; }
    public boolean isOperouAntes() { return operouAntes; }
    public void setOperouAntes(boolean operouAntes) { this.operouAntes = operouAntes; }
    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public List<CadastroCastracao> getHistoricoVida() { return historicoVida; }
    public void setHistoricoVida(List<CadastroCastracao> historicoVida) { this.historicoVida = historicoVida; }
}