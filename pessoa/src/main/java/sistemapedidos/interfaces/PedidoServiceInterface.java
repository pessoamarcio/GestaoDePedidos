package sistemapedidos.interfaces;

import sistemapedidos.model.Pedido;

import java.util.Map;
import java.util.UUID;

public interface PedidoServiceInterface {
	Pedido criarPedido(UUID clienteId, Map<UUID, Integer> itens);
	Pedido buscarPorId(UUID id);
	Pedido substituirItens(UUID pedidoId, Map<UUID, Integer> itens);
	Pedido pagar(UUID pedidoId);
	Pedido cancelar(UUID pedidoId);
}
