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

    // --- NOVOS CAMPOS PARA AUDITORIA E EXTRATO ---

    @ManyToOne
    @JoinColumn(name = "voluntario_aprovador_id")
// Removi o "administrador" daqui. Agora ele aparece, mas a senha (que está com @JsonIgnore) não.
    @JsonIgnoreProperties({"dataCadastro", "hibernateLazyInitializer", "handler"})
    private Voluntario aprovadoPor;
    private String aprovadorNome; // String simples para auditoria visual

    @ManyToOne
    @JoinColumn(name = "pix_config_id")
    private ConfiguracaoPix contaDestino;

    public Pagamento() {}

    // --- GETTERS E SETTERS ATUALIZADOS ---

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

    public Voluntario getAprovadoPor() { return aprovadoPor; }
    public void setAprovadoPor(Voluntario aprovadoPor) { this.aprovadoPor = aprovadoPor; }

    public String getAprovadorNome() {
        return aprovadorNome;
    }

    public void setAprovadorNome(String aprovadorNome) {
        this.aprovadorNome = aprovadorNome;
    }

    public ConfiguracaoPix getContaDestino() { return contaDestino; }
    public void setContaDestino(ConfiguracaoPix contaDestino) { this.contaDestino = contaDestino; }
}