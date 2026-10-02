package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa a Sessão Ativa (Comanda Geral) vinculada a uma mesa física.
 * Gerencia múltiplos clientes (Subcomandas) compartilhando o mesmo espaço.
 */
@Entity
@Table(name = "sessoes_mesa")
public class SessaoMesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSessao status = StatusSessao.ABERTA;

    @Column(name = "data_abertura", nullable = false, updatable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    @OneToMany(mappedBy = "sessaoMesa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subcomanda> subcomandas = new ArrayList<>();

    public SessaoMesa() {
        this.dataAbertura = LocalDateTime.now();
    }

    public SessaoMesa(Mesa mesa) {
        this();
        this.mesa = mesa;
    }

    public void adicionarSubcomanda(Subcomanda subcomanda) {
        subcomandas.add(subcomanda);
        subcomanda.setSessaoMesa(this);
    }

    public void encerrarSessao() {
        this.status = StatusSessao.ENCERRADA;
        this.dataEncerramento = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public StatusSessao getStatus() { return status; }
    public void setStatus(StatusSessao status) { this.status = status; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }

    public List<Subcomanda> getSubcomandas() { return subcomandas; }
    public void setSubcomandas(List<Subcomanda> subcomandas) { this.subcomandas = subcomandas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessaoMesa that = (SessaoMesa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
