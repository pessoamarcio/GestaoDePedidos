package sistemapedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByLogin(String login);
	boolean existsByLogin(String login);
}

