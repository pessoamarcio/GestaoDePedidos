package sistemapedidos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sistemapedidos.model.UserSession;
import sistemapedidos.model.Usuario;
import sistemapedidos.repository.UserSessionRepository;

import java.time.Instant;

@Service
public class UserSessionService {

	private final UserSessionRepository userSessionRepository;
	private final long expiresInSeconds;

	public UserSessionService(
			UserSessionRepository userSessionRepository,
			@Value("${app.session.expires-in-seconds:300}") long expiresInSeconds
	) {
		this.userSessionRepository = userSessionRepository;
		this.expiresInSeconds = expiresInSeconds;
	}

	@Transactional
	public UserSession issue(Usuario usuario) {
		Instant now = Instant.now();
		UserSession session = new UserSession(usuario, now, now.plusSeconds(expiresInSeconds));
		return userSessionRepository.save(session);
	}

	@Transactional(readOnly = true)
	public UserSession validate(String sessionId) {
		Instant now = Instant.now();
		UserSession session = userSessionRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new IllegalStateException("Sessão inválida."));
		if (!session.isActive(now)) {
			throw new IllegalStateException("Sessão inválida.");
		}
		return session;
	}

	@Transactional
	public void revoke(String sessionId) {
		UserSession session = validate(sessionId);
		session.revoke(Instant.now());
		userSessionRepository.save(session);
	}
}
