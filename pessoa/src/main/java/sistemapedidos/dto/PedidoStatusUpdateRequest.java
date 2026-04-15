package sistemapedidos.dto;

import jakarta.validation.constraints.NotNull;
import sistemapedidos.model.enums.StatusPedido;

public record PedidoStatusUpdateRequest(
        @NotNull(message = "status é obrigatório")
        StatusPedido status
) {
}
