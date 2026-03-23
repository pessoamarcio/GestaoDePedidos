package sistemapedidos.dto;

import sistemapedidos.model.ItemPedido;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        UUID clienteId,
        StatusPedido status,
        OffsetDateTime criadoEm,
        BigDecimal valorTotal,
        List<ItemResponse> itens
) {
    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                pedido.getValorTotal(),
                pedido.getItens().stream().map(ItemResponse::from).toList()
        );
    }

    public record ItemResponse(
            UUID produtoId,
            String produtoNome,
            int quantidade,
            BigDecimal valorDaCompra,
            BigDecimal valorTotal
    ) {
        static ItemResponse from(ItemPedido item) {
            return new ItemResponse(
                    item.getProduto().getId(),
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    item.getValorDaCompra(),
                    item.getValorTotal()
            );
        }
    }
}
