package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import jakarta.persistence.*;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMesa status;

    public Mesa() {}

    public Mesa(Integer numero, Integer capacidade) {
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = StatusMesa.DISPONIVEL;
    }

    // Getters e Setters (Dica: clique com o botão direito no código > Source > Generate Getters and Setters > Select All > Generate)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }
}
