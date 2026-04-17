package sistemapedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

	boolean existsByEmailIgnoreCase(String email);
	boolean existsByCpf(String cpf);
	Optional<Cliente> findByCpf(String cpf);
	List<Cliente> findByNomeContainingIgnoreCase(String nome);
}
