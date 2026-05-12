package sistemapedidos.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoItensRequest(
        @NotEmpty(message = "itens é obrigatório")
        List<@NotNull PedidoItemRequest> itens
) {
}
