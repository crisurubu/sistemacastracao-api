package com.projetoong.sistema_castracao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinicas")
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cnpj;

    @Column(nullable = false)
    private String crmvResponsavel;

    private String telefone;

    // --- ENDEREÇO ESTRUTURADO ---
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    @Column(unique = true)
    private String email;

    private int totalCastracoes = 0;

    @Enumerated(EnumType.STRING)
    private SeloParceiro selo = SeloParceiro.INICIANTE;

    // NOVO CAMPO: Data de Cadastro (Igual ao Voluntário)
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "ativo")
    private boolean ativo = true;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Administrador administrador;

    // Construtor atualizado para inicializar a data automaticamente
    public Clinica() {
        this.dataCadastro = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // (Mantenha todos os outros getters e setters que você já tem abaixo...)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getCrmvResponsavel() { return crmvResponsavel; }
    public void setCrmvResponsavel(String crmvResponsavel) { this.crmvResponsavel = crmvResponsavel; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getTotalCastracoes() { return totalCastracoes; }
    public void setTotalCastracoes(int totalCastracoes) { this.totalCastracoes = totalCastracoes; }
    public SeloParceiro getSelo() { return selo; }
    public void setSelo(SeloParceiro selo) { this.selo = selo; }
    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
}