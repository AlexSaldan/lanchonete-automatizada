package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa um item solicitado por um cliente em sua subcomanda.
 * 
 * Boas Práticas de Engenharia:
 * - O campo 'precoUnitario' captura o valor do produto no momento do pedido (Snapshot),
 *   garantindo integridade auditável e imutabilidade do valor histórico.
 */
@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subcomanda_id", nullable = false)
    private Subcomanda subcomanda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(length = 255)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_preparo", nullable = false, length = 20)
    private StatusPreparo statusPreparo = StatusPreparo.RECEBIDO;

    @Column(name = "data_hora_pedido", nullable = false, updatable = false)
    private LocalDateTime dataHoraPedido;

    public ItemPedido() {
        this.dataHoraPedido = LocalDateTime.now();
    }

    public ItemPedido(Subcomanda subcomanda, Produto produto, Integer quantidade, String observacao) {
        this();
        this.subcomanda = subcomanda;
        this.produto = produto;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.precoUnitario = produto.getPreco(); // Tira o snapshot do preço atual do produto
    }

    /**
     * Calcula o subtotal do item multiplicando a quantidade pelo preço unitario registrado.
     * 
     * @return BigDecimal contendo o valor total do item
     */
    public BigDecimal getSubtotal() {
        if (precoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subcomanda getSubcomanda() { return subcomanda; }
    public void setSubcomanda(Subcomanda subcomanda) { this.subcomanda = subcomanda; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public StatusPreparo getStatusPreparo() { return statusPreparo; }
    public void setStatusPreparo(StatusPreparo statusPreparo) { this.statusPreparo = statusPreparo; }

    public LocalDateTime getDataHoraPedido() { return dataHoraPedido; }
    public void setDataHoraPedido(LocalDateTime dataHoraPedido) { this.dataHoraPedido = dataHoraPedido; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPedido item = (ItemPedido) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
