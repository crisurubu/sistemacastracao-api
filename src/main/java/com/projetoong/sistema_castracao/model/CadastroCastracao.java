package com.projetoong.sistema_castracao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cadastros")
public class CadastroCastracao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataSolicitacao = LocalDateTime.now();
    private String statusProcesso = "AGUARDANDO_PAGAMENTO";

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    // ADICIONE ESTE CAMPO AQUI:
    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @OneToOne(mappedBy = "cadastro", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    public CadastroCastracao() {}

    // --- GETTERS E SETTERS CORRIGIDOS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getStatusProcesso() { return statusProcesso; }
    public void setStatusProcesso(String statusProcesso) { this.statusProcesso = statusProcesso; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    // ADICIONE ESTES DOIS MÉTODOS:
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
}