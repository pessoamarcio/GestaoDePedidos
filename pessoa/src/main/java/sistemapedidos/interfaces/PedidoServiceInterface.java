package sistemapedidos.interfaces;

import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.enums.StatusPedido;

import java.util.List;
import java.util.UUID;

public interface PedidoServiceInterface {
    Pedido criarPedido(PedidoCreateRequest request);
    Pedido buscarPorId(UUID id);
    List<Pedido> buscarPorCpfCliente(String cpf);
    Pedido atualizarStatus(UUID pedidoId, StatusPedido status);
}
