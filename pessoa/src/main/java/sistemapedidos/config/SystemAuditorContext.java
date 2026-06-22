package sistemapedidos.config;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SystemAuditorContext {

	private final AtomicReference<Long> systemUserId = new AtomicReference<>();

	public void setSystemUserId(Long id) {
		systemUserId.set(id);
	}

	public Optional<Long> getSystemUserId() {
		return Optional.ofNullable(systemUserId.get());
	}
}
