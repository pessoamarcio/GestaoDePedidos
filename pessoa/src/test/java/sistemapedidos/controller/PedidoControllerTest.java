package sistemapedidos.controller;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.dto.PedidoItemRequest;
import sistemapedidos.dto.PedidoItensRequest;
import sistemapedidos.dto.PedidoResponse;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.PedidoServiceInterface;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.ItemPedido;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.model.enums.StatusPedido;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoServiceInterface pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @Test
    void criarDeveRetornarCreatedComPedidoResponse() {
        Pedido pedido = criarPedidoComItem();
        UUID clienteId = pedido.getCliente().getId();
        UUID produtoId = pedido.getItens().getFirst().getProduto().getId();
        PedidoCreateRequest request = new PedidoCreateRequest(
                clienteId,
                List.of(new PedidoItemRequest(produtoId, 2))
        );
        when(pedidoService.criarPedido(request)).thenReturn(pedido);

        ResponseEntity<PedidoResponse> response = pedidoController.criar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pedido.getId(), response.getBody().id());
        assertEquals(clienteId, response.getBody().clienteId());
    }

    @Test
    void buscarPorIdDeveRetornarPedidoResponse() {
        Pedido pedido = criarPedidoComItem();
        when(pedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);

        PedidoResponse response = pedidoController.buscarPorId(pedido.getId());

        assertEquals(pedido.getId(), response.id());
        assertEquals(StatusPedido.CRIADO, response.status());
        assertEquals(1, response.itens().size());
    }

    @Test
    void substituirItensDeveSomarQuantidadesPorProduto() {
        Pedido pedido = criarPedidoComItem();
        UUID pedidoId = pedido.getId();
        UUID produtoId = pedido.getItens().getFirst().getProduto().getId();
        PedidoItensRequest request = new PedidoItensRequest(List.of(
                new PedidoItemRequest(produtoId, 2),
                new PedidoItemRequest(produtoId, 3)
        ));
        when(pedidoService.substituirItens(pedidoId, Map.of(produtoId, 5))).thenReturn(pedido);

        PedidoResponse response = pedidoController.substituirItens(pedidoId, request);

        assertEquals(pedidoId, response.id());
        verify(pedidoService).substituirItens(pedidoId, Map.of(produtoId, 5));
    }

    @Test
    void substituirItensDeveFalharQuandoItemNaoTemProduto() {
        UUID pedidoId = UUID.randomUUID();
        PedidoItensRequest request = new PedidoItensRequest(List.of(new PedidoItemRequest(null, 2)));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> pedidoController.substituirItens(pedidoId, request)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("produto"));
    }

    @Test
    void pagarDeveDelegarAoService() {
        Pedido pedido = criarPedidoComItem();
        when(pedidoService.pagar(pedido.getId())).thenReturn(pedido);

        PedidoResponse response = pedidoController.pagar(pedido.getId());

        assertEquals(pedido.getId(), response.id());
        verify(pedidoService).pagar(pedido.getId());
    }

    @Test
    void cancelarDeveDelegarAoService() {
        Pedido pedido = criarPedidoComItem();
        when(pedidoService.cancelar(pedido.getId())).thenReturn(pedido);

        PedidoResponse response = pedidoController.cancelar(pedido.getId());

        assertEquals(pedido.getId(), response.id());
        verify(pedidoService).cancelar(pedido.getId());
    }

    private static Pedido criarPedidoComItem() {
        UUID pedidoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Cliente cliente = new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO);
        TestReflectionUtils.setField(cliente, "id", clienteId);

        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        TestReflectionUtils.setField(produto, "id", produtoId);

        Pedido pedido = new Pedido(cliente);
        TestReflectionUtils.setField(pedido, "id", pedidoId);

        pedido.adicionarItem(new ItemPedido(produto, 2, new BigDecimal("4999.90")));
        return pedido;
    }
}
