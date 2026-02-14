package com.projetoong.sistema_castracao.model;

import jakarta.persistence.*;

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
    private String endereco;

    @Column(unique = true)
    private String email;

    private int totalCastracoes = 0;

    // NOVO CAMPO: Persistido no banco para performance e consultas SQL
    @Enumerated(EnumType.STRING)
    private SeloParceiro selo = SeloParceiro.INICIANTE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Administrador administrador;

    public Clinica() {}

    // --- GETTERS E SETTERS ---
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

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getTotalCastracoes() { return totalCastracoes; }
    public void setTotalCastracoes(int totalCastracoes) { this.totalCastracoes = totalCastracoes; }

    public SeloParceiro getSelo() { return selo; }
    public void setSelo(SeloParceiro selo) { this.selo = selo; }

    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
}