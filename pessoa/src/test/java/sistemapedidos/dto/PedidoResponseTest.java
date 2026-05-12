package sistemapedidos.dto;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.ItemPedido;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoResponseTest {

    @Test
    void fromDeveMapearPedidoEItens() {
        UUID pedidoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", StatusCliente.ATIVO);
        TestReflectionUtils.setField(cliente, "id", clienteId);

        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        TestReflectionUtils.setField(produto, "id", produtoId);

        Pedido pedido = new Pedido(cliente);
        TestReflectionUtils.setField(pedido, "id", pedidoId);
        pedido.adicionarItem(new ItemPedido(produto, 2, new BigDecimal("4999.90")));

        PedidoResponse response = PedidoResponse.from(pedido);

        assertEquals(pedidoId, response.id());
        assertEquals(clienteId, response.clienteId());
        assertEquals("Maria", response.clienteNome());
        assertEquals(new BigDecimal("9999.80"), response.valorTotal());
        assertEquals(1, response.itens().size());
        assertEquals(produtoId, response.itens().getFirst().produtoId());
        assertEquals("Notebook", response.itens().getFirst().produtoNome());
    }
}
