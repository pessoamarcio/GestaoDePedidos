package sistemapedidos.dto.auth;

public record AuthTokenResponse(
		String tokenType,
		String accessToken,
		long expiresInSeconds
) {
	public static AuthTokenResponse bearer(String token, long expiresInSeconds) {
		return new AuthTokenResponse("Bearer", token, expiresInSeconds);
	}
}

