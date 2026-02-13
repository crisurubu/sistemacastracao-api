package com.projetoong.sistema_castracao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "administradores")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role nivelAcesso;

    // --- NOVO CAMPO ADICIONADO ---
    @Column(nullable = false)
    private boolean ativo = true;

    // --- CONSTRUTORES ---
    public Administrador() {
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(Role nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    // --- NOVO GETTER E SETTER ---
    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}