package sistemapedidos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistemapedidos.dto.auth.AuthMessageResponse;
import sistemapedidos.dto.auth.AuthForgotPasswordRequest;
import sistemapedidos.dto.auth.AuthUserCreateRequest;
import sistemapedidos.dto.auth.AuthRegisterResponse;
import sistemapedidos.model.Usuario;
import sistemapedidos.model.enums.PerfilUsuario;
import sistemapedidos.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAuthServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UsuarioAuthService usuarioAuthService;

	@Test
	void forgotPasswordDeveAtualizarSenhaQuandoUsuarioExistir() {
		Usuario usuario = new Usuario("admin", "hash-antigo", PerfilUsuario.ADMIN);
		when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.encode("Nova@@123")).thenReturn("hash-novo");

		AuthMessageResponse response = usuarioAuthService.forgotPassword(new AuthForgotPasswordRequest("admin", "Nova@@123"));

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepository).save(captor.capture());
		assertEquals("hash-novo", captor.getValue().getPasswordHash());
		assertEquals("Senha alterada com sucesso.", response.mensagem());
	}

	@Test
	void forgotPasswordDeveLancarQuandoUsuarioNaoExistir() {
		when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

		assertThrows(org.springframework.web.server.ResponseStatusException.class,
				() -> usuarioAuthService.forgotPassword(new AuthForgotPasswordRequest("inexistente", "Nova@@123")));
	}

	@Test
	void createUserDeveSalvarComPerfilInformado() {
		when(usuarioRepository.existsByLogin("operador")).thenReturn(false);
		when(passwordEncoder.encode("Senha@@123")).thenReturn("hash-novo");
		when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		AuthRegisterResponse response = usuarioAuthService.createUser(new AuthUserCreateRequest("operador", "Senha@@123", PerfilUsuario.OPERADOR));

		assertEquals(PerfilUsuario.OPERADOR, response.perfil());
	}
}
