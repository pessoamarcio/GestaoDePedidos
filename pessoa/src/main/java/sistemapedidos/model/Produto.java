package sistemapedidos.model;

import sistemapedidos.model.enums.StatusProduto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "produtos")
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal preco;

	@Column(nullable = false)
	private int quantidadeEmEstoque;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusProduto status = StatusProduto.DISPONIVEL;

	protected Produto() {
	}

	public Produto(String nome, BigDecimal preco, int quantidadeEmEstoque, StatusProduto status) {
		this.nome = nome;
		this.preco = preco;
		this.quantidadeEmEstoque = quantidadeEmEstoque;
		this.status = status == null ? StatusProduto.DISPONIVEL : status;
	}

	public UUID getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public int getQuantidadeEmEstoque() {
		return quantidadeEmEstoque;
	}

	public StatusProduto getStatus() {
		return status;
	}

	public boolean podeVender(int quantidade) {
		return status == StatusProduto.DISPONIVEL && quantidadeEmEstoque >= quantidade;
	}

	public void baixarEstoque(int quantidade) {
		this.quantidadeEmEstoque -= quantidade;
	}

	public void devolverEstoque(int quantidade) {
		this.quantidadeEmEstoque += quantidade;
	}
}
