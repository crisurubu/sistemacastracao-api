package com.projetoong.sistema_castracao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comprovanteUrl;
    private boolean confirmado = false;
    private Double valorContribuicao;
    private LocalDateTime dataConfirmacao;

    @OneToOne
    @JoinColumn(name = "cadastro_id")
    @JsonIgnoreProperties({"pagamento", "handler", "hibernateLazyInitializer"})
    private CadastroCastracao cadastro;

    public Pagamento() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComprovanteUrl() { return comprovanteUrl; }
    public void setComprovanteUrl(String comprovanteUrl) { this.comprovanteUrl = comprovanteUrl; }
    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
    public Double getValorContribuicao() { return valorContribuicao; }
    public void setValorContribuicao(Double valorContribuicao) { this.valorContribuicao = valorContribuicao; }
    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDateTime dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }
    public CadastroCastracao getCadastro() { return cadastro; }
    public void setCadastro(CadastroCastracao cadastro) { this.cadastro = cadastro; }
}