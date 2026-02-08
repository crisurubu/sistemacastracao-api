package com.projetoong.sistema_castracao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aqui está o segredo: O agendamento conhece o cadastro todo (Pet + Tutor)
    @OneToOne
    @JoinColumn(name = "cadastro_id", nullable = false)
    private CadastroCastracao cadastro;

    private LocalDateTime dataHora;
    private String local;
    private String codigoHash; // O código para o PDF/E-mail

    private boolean realizado = false;
    private LocalDateTime dataRegistro = LocalDateTime.now();

    public Agendamento() {}

    // Getters e Setters...
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