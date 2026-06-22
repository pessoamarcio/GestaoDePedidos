package sistemapedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import sistemapedidos.model.Usuario;
import sistemapedidos.config.SystemAuditorContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public AuditorAware<Usuario> auditorAware(SystemAuditorContext systemAuditorContext) {
		return () -> currentUserId()
				.map(id -> entityManager.getReference(Usuario.class, id))
				.or(() -> systemAuditorContext.getSystemUserId().map(id -> entityManager.getReference(Usuario.class, id)));
	}

	private Optional<Long> currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof Jwt jwt) {
			Long uid = jwt.getClaim("uid");
			if (uid != null) {
				return Optional.of(uid);
			}
		}
		return Optional.empty();
	}
}
