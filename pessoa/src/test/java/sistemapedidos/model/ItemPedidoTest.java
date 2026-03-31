package sistemapedidos.model;

import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemPedidoTest {

    @Test
    void getValorTotalDeveMultiplicarValorPelaQuantidade() {
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        ItemPedido item = new ItemPedido(produto, 3, new BigDecimal("10.50"));

        assertEquals(new BigDecimal("31.50"), item.getValorTotal());
    }
}
