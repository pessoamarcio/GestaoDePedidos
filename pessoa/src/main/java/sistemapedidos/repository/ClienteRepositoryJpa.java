package sistemapedidos.repository;

import sistemapedidos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepositoryJpa extends JpaRepository<Cliente, UUID> {
	boolean existsByEmailIgnoreCase(String email);
}
