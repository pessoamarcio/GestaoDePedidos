package sistemapedidos.dto;

import sistemapedidos.model.enums.StatusCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteCreateRequest(
		@NotBlank(message = "nome é obrigatório")
		String nome,
		@NotBlank(message = "email é obrigatório")
		@Email(message = "email inválido")
		String email,
		StatusCliente status
) {}
