package sistemapedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Cliente;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

	boolean existsByEmailIgnoreCase(String email);
}
