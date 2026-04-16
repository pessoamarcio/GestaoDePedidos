package sistemapedidos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import sistemapedidos.model.enums.StatusCliente;

public record ClienteCreateRequest(
        @NotBlank(message = "nome é obrigatório")
        String nome,
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 números")
        String cpf,
        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,
        StatusCliente status
) {}
