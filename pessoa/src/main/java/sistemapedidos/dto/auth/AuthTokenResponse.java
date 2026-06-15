package sistemapedidos.dto.auth;

public record AuthTokenResponse(
	String tokenType,
	String accessToken,
	long expiresInSeconds,
	String mensagem
) {
	public static AuthTokenResponse bearer(String accessToken, long expiresInSeconds) {
		return new AuthTokenResponse("Bearer", accessToken, expiresInSeconds, "Login realizado com sucesso.");
	}
}
