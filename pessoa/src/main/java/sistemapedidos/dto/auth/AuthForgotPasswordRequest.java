package sistemapedidos.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import sistemapedidos.config.security.ValidPassword;

public record AuthForgotPasswordRequest(
		@NotBlank
		@Size(max = 100, message = "username deve ter no maximo 100 caracteres.")
		String username,

		@NotBlank
		@ValidPassword
		String newPassword
) {
}
