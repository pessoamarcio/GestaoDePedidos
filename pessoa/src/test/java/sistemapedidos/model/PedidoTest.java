package sistemapedidos.model;

import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.model.enums.StatusPedido;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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

    @Test
    void novoPedidoDeveIniciarAguardandoPagamento() {
        Pedido pedido = novoPedido();

        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
    }

    private static Pedido novoPedido() {
        return new Pedido(new Cliente("Maria", "maria@email.com", "12345678901", StatusCliente.ATIVO));
    }

    private static ItemPedido novoItem(String nome, String valor, int quantidade) {
        Produto produto = new Produto(nome, new BigDecimal(valor), 10, StatusProduto.DISPONIVEL);
        return new ItemPedido(produto, quantidade, new BigDecimal(valor));
    }
}
