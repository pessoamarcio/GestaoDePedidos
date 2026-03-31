package sistemapedidos.interfaces;

import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.model.Pedido;

import java.util.Map;
import java.util.UUID;

public interface PedidoServiceInterface {
	Pedido criarPedido(PedidoCreateRequest request);
	Pedido buscarPorId(UUID id);
	Pedido substituirItens(UUID pedidoId, Map<UUID, Integer> itens);
	Pedido pagar(UUID pedidoId);
	Pedido cancelar(UUID pedidoId);
}
