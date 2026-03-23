package sistemapedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PedidoItemRequest(
        @NotNull(message = "produtoId é obrigatório")
        @Schema(description = "UUID do produto", example = "")
        UUID produtoId,
        @Positive(message = "quantidade deve ser > 0")
        @Schema(description = "Quantidade do produto", example = "1", minimum = "1")
        int quantidade
) {
}
