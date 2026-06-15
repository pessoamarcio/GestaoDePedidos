package sistemapedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sistemapedidos.model.UserSession;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
	@Query("select s from UserSession s join fetch s.usuario where s.sessionId = :sessionId")
	Optional<UserSession> findBySessionId(String sessionId);
}
