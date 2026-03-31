package sistemapedidos.model;

import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.model.enums.StatusPedido;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoTest {

    @Test
    void adicionarItemEValorTotalDevemRefletirItensDoPedido() {
        Pedido pedido = novoPedido();
        pedido.adicionarItem(novoItem("Notebook", "4999.90", 2));
        pedido.adicionarItem(novoItem("Mouse", "100.00", 1));

        assertEquals(2, pedido.getItens().size());
        assertEquals(new BigDecimal("10099.80"), pedido.getValorTotal());
    }

    @Test
    void substituirItensDeveTrocarColecaoQuandoPedidoEstaCriado() {
        Pedido pedido = novoPedido();
        pedido.adicionarItem(novoItem("Notebook", "4999.90", 1));

        pedido.substituirItens(List.of(novoItem("Mouse", "100.00", 3)));

        assertEquals(1, pedido.getItens().size());
        assertEquals(new BigDecimal("300.00"), pedido.getValorTotal());
    }

    @Test
    void substituirItensDeveFalharQuandoPedidoPagoOuCancelado() {
        Pedido pago = novoPedido();
        pago.pagar();
        Pedido cancelado = novoPedido();
        cancelado.cancelar();

        assertThrows(IllegalStateException.class, () -> pago.substituirItens(List.of(novoItem("Mouse", "100.00", 1))));
        assertThrows(IllegalStateException.class, () -> cancelado.substituirItens(List.of(novoItem("Mouse", "100.00", 1))));
    }

    @Test
    void pagarECancelarDevemAtualizarStatusCorretamente() {
        Pedido pedido = novoPedido();
        pedido.pagar();

        assertTrue(pedido.estaPago());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertThrows(IllegalStateException.class, pedido::cancelar);
    }

    @Test
    void cancelarDeveMarcarPedidoECanceladoSemFalharNaSegundaVez() {
        Pedido pedido = novoPedido();

        pedido.cancelar();
        pedido.cancelar();

        assertTrue(pedido.estaCancelado());
        assertFalse(pedido.estaPago());
        assertThrows(IllegalStateException.class, pedido::pagar);
    }

    private static Pedido novoPedido() {
        return new Pedido(new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO));
    }

    private static ItemPedido novoItem(String nome, String valor, int quantidade) {
        Produto produto = new Produto(nome, new BigDecimal(valor), 10, StatusProduto.DISPONIVEL);
        return new ItemPedido(produto, quantidade, new BigDecimal(valor));
    }
}
