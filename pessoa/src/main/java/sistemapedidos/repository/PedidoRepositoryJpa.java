package sistemapedidos.repository;

import sistemapedidos.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepositoryJpa extends JpaRepository<Pedido, UUID> {

	@Override
	@EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
	Optional<Pedido> findById(UUID id);
}
