package com.projetoong.sistema_castracao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sistema_configs")
public class SistemaConfig {
    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private boolean cadastroAberto = false;

    private String mensagemFechado = "Inscrições encerradas no momento.";

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCadastroAberto() {
        return cadastroAberto;
    }

    public void setCadastroAberto(boolean cadastroAberto) {
        this.cadastroAberto = cadastroAberto;
    }

    public String getMensagemFechado() {
        return mensagemFechado;
    }

    public void setMensagemFechado(String mensagemFechado) {
        this.mensagemFechado = mensagemFechado;
    }
}