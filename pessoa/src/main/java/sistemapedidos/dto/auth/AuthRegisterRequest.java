package sistemapedidos.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import sistemapedidos.config.security.ValidPassword;

public record AuthRegisterRequest(
		@NotBlank
		@Size(max = 100, message = "username deve ter no máximo 100 caracteres.")
		String username,

		@NotBlank
		@ValidPassword
		String password
) {
}

