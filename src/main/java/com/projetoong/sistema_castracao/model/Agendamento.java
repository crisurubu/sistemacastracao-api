package com.projetoong.sistema_castracao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cadastro_id", nullable = false)
    private CadastroCastracao cadastro;

    // --- O QUE FALTAVA: Relacionamento com a Clínica ---
    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    private LocalDateTime dataHora;
    private String local; // Mantemos para o endereço por extenso
    private String codigoHash;

    private boolean realizado = false;
    private LocalDateTime dataRegistro = LocalDateTime.now();

    public Agendamento() {}

    // --- NOVOS GETTERS E SETTERS DA CLÍNICA ---
    public Clinica getClinica() {
        return clinica;
    }

    public void setClinica(Clinica clinica) {
        this.clinica = clinica;
    }

    // --- GETTERS E SETTERS QUE VOCÊ JÁ TINHA ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CadastroCastracao getCadastro() { return cadastro; }
    public void setCadastro(CadastroCastracao cadastro) { this.cadastro = cadastro; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getCodigoHash() { return codigoHash; }
    public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }
    public boolean isRealizado() { return realizado; }
    public void setRealizado(boolean realizado) { this.realizado = realizado; }
}