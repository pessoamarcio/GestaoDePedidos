package sistemapedidos.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Pedido;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
    Optional<Pedido> findById(UUID id);

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
    List<Pedido> findAllByClienteCpfOrderByCriadoEmDesc(String cpf);
}
