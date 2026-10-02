package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa a comanda individual de um cliente específico na mesa.
 * Suporta o split de contas (divisão de pagamento) independente.
 */
@Entity
@Table(name = "subcomandas")
public class Subcomanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_mesa_id", nullable = false)
    private SessaoMesa sessaoMesa;

    @Column(name = "nome_cliente", nullable = false, length = 100)
    private String nomeCliente;

    @Column(name = "cpf_cliente", length = 14)
    private String cpfCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 20)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public Subcomanda() {}

    public Subcomanda(String nomeCliente, String cpfCliente) {
        this.nomeCliente = nomeCliente;
        this.cpfCliente = cpfCliente;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SessaoMesa getSessaoMesa() { return sessaoMesa; }
    public void setSessaoMesa(SessaoMesa sessaoMesa) { this.sessaoMesa = sessaoMesa; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }

    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(StatusPagamento statusPagamento) { this.statusPagamento = statusPagamento; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subcomanda that = (Subcomanda) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
