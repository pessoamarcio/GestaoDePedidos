package sistemapedidos.dto;

import jakarta.validation.constraints.Positive;

public record ProdutoEstoqueRequest(
        @Positive(message = "quantidade deve ser maior que 0")
        int quantidade
) {
}
