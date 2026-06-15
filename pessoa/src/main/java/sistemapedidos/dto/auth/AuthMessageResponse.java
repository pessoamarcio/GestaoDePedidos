package sistemapedidos.dto.auth;

public record AuthMessageResponse(String mensagem) {
	public static AuthMessageResponse of(String mensagem) {
		return new AuthMessageResponse(mensagem);
	}
}
