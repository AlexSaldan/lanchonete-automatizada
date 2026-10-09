package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de Domínio que representa uma Mesa física do estabelecimento.
 * 
 * Engenharia de Segurança:
 * - O campo 'tokenQrCode' funciona como um Token de Proximidade (Proximity Token).
 * - Impede manipulação maliciosa de URLs (Insecure Direct Object References - IDOR),
 *   garantindo que apenas clientes fisicamente na mesa consigam abrir ou acessar a sessão.
 */
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
    @Column(nullable = false, length = 20)
    private StatusMesa status = StatusMesa.DISPONIVEL;

    @Column(name = "token_qr_code", nullable = false, unique = true, length = 36)
    private String tokenQrCode;

    public Mesa() {
        this.tokenQrCode = UUID.randomUUID().toString();
    }

    public Mesa(Integer numero, Integer capacidade) {
        this();
        this.numero = numero;
        this.capacidade = capacidade;
    }

    /**
     * Regenera o token do QR Code da mesa. 
     * Deve ser invocado sempre que a mesa for liberada, invalidando acessos antigos.
     */
    public void regenerarTokenQrCode() {
        this.tokenQrCode = UUID.randomUUID().toString();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }

    public String getTokenQrCode() { return tokenQrCode; }
    public void setTokenQrCode(String tokenQrCode) { this.tokenQrCode = tokenQrCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mesa mesa = (Mesa) o;
        return Objects.equals(id, mesa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
