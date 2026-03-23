package sistemapedidos.repository;

import sistemapedidos.model.Pedido;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PedidoRepository {

	private final PedidoRepositoryJpa pedidoRepositoryJpa;

	public PedidoRepository(PedidoRepositoryJpa pedidoRepositoryJpa) {
		this.pedidoRepositoryJpa = pedidoRepositoryJpa;
	}

	public Pedido save(Pedido pedido) {
		return pedidoRepositoryJpa.save(pedido);
	}

	public Optional<Pedido> findById(UUID id) {
		return pedidoRepositoryJpa.findById(id);
	}
}
