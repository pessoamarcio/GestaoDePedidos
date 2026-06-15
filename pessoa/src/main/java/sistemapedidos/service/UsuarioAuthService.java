package sistemapedidos.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sistemapedidos.dto.auth.AuthRegisterRequest;
import sistemapedidos.dto.auth.AuthRegisterResponse;
import sistemapedidos.model.Usuario;
import sistemapedidos.repository.UsuarioRepository;

@Service
public class UsuarioAuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioAuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public AuthRegisterResponse register(AuthRegisterRequest request) {
		String login = request.username();

		if (usuarioRepository.existsByLogin(login)) {
			throw new IllegalStateException("Usuário já existe.");
		}

		String hash = passwordEncoder.encode(request.password());
		Usuario usuario = usuarioRepository.save(new Usuario(login, hash));
		return AuthRegisterResponse.of(usuario.getId(), usuario.getLogin());
	}
}

