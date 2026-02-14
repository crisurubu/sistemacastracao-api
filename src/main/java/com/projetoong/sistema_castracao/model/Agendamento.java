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

    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    private LocalDateTime dataHora;
    private String local;
    private String codigoHash;

    private boolean realizado = false;
    private LocalDateTime dataRegistro = LocalDateTime.now();

    // --- CAMPOS DE AUDITORIA (Resolve o erro 403 e permite carimbo do Master) ---
    @ManyToOne
    @JoinColumn(name = "voluntario_agendador_id")
    private Voluntario agendadoPor; // FK para voluntário pessoa física

    @Column(name = "agendador_nome")
    private String agendadorNome; // Nome textual (Ex: "SISTEMA MASTER (ONG)")

    // Construtor padrão
    public Agendamento() {}

    // --- GETTERS E SETTERS ORGANIZADOS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CadastroCastracao getCadastro() { return cadastro; }
    public void setCadastro(CadastroCastracao cadastro) { this.cadastro = cadastro; }

    public Clinica getClinica() { return clinica; }
    public void setClinica(Clinica clinica) { this.clinica = clinica; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getCodigoHash() { return codigoHash; }
    public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }

    public boolean isRealizado() { return realizado; }
    public void setRealizado(boolean realizado) { this.realizado = realizado; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    // Getters e Setters de Auditoria
    public Voluntario getAgendadoPor() { return agendadoPor; }
    public void setAgendadoPor(Voluntario agendadoPor) { this.agendadoPor = agendadoPor; }

    public String getAgendadorNome() { return agendadorNome; }
    public void setAgendadorNome(String agendadorNome) { this.agendadorNome = agendadorNome; }
}