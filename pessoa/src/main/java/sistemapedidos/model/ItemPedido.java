package sistemapedidos.model;

import sistemapedidos.model.Produto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorDaCompra;

    protected ItemPedido() {
    }

    public ItemPedido(Produto produto, int quantidade, BigDecimal valorDaCompra) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorDaCompra = valorDaCompra;
    }

    void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorDaCompra() {
        return valorDaCompra;
    }

    public BigDecimal getValorTotal() {
        return valorDaCompra.multiply(BigDecimal.valueOf(quantidade));
    }
}
