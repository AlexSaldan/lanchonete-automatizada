package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @NotNull(message = "A categoria é obrigatória")
    @Enumerated(EnumType.STRING) // Salva "LANCHE" no banco, não "0"
    @Column(nullable = false)
    private CategoriaProduto categoria;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "O tempo de preparo é obrigatório")
    @PositiveOrZero(message = "O tempo de preparo não pode ser negativo")
    @Column(nullable = false)
    private Integer tempoPreparoMinutos;

    @Column(nullable = false)
    private Boolean disponivel = true;

    // Construtor padrão (Obrigatório para o JPA)
    public Produto() {}

    // Construtor completo (Facilita a criação em testes e no DataLoader)
    public Produto(String nome, String descricao, CategoriaProduto categoria, BigDecimal preco, Integer tempoPreparoMinutos) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.tempoPreparoMinutos = tempoPreparoMinutos;
        this.disponivel = true;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public CategoriaProduto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProduto categoria) { this.categoria = categoria; }
    
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    
    public Integer getTempoPreparoMinutos() { return tempoPreparoMinutos; }
    public void setTempoPreparoMinutos(Integer tempoPreparoMinutos) { this.tempoPreparoMinutos = tempoPreparoMinutos; }
    
    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
}
