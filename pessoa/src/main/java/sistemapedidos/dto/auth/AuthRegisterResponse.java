package sistemapedidos.dto.auth;

public record AuthRegisterResponse(
		Long id,
		String username
) {
	public static AuthRegisterResponse of(Long id, String username) {
		return new AuthRegisterResponse(id, username);
	}
}

