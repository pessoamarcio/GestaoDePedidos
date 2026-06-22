package sistemapedidos.dto.auth;

import sistemapedidos.model.enums.PerfilUsuario;

public record AuthRegisterResponse(
		Long id,
		String username,
		PerfilUsuario perfil
) {
	public static AuthRegisterResponse of(Long id, String username, PerfilUsuario perfil) {
		return new AuthRegisterResponse(id, username, perfil);
	}
}
