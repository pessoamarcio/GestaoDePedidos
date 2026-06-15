package sistemapedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequest(
		@NotBlank(message = "logradouro é obrigatório")
		String logradouro,
		@NotBlank(message = "numero é obrigatório")
		String numero,
		String complemento,
		@NotBlank(message = "bairro é obrigatório")
		String bairro,
		@NotBlank(message = "cidade é obrigatória")
		String cidade,
		@NotBlank(message = "estado é obrigatório")
		@Pattern(regexp = "[A-Za-z]{2}", message = "estado deve conter 2 letras")
		String estado,
		@NotBlank(message = "cep é obrigatório")
		@Pattern(regexp = "\\d{8}", message = "cep deve conter 8 números")
		String cep
) {}
