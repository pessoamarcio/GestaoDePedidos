package sistemapedidos.controller;

import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.dto.PedidoItemRequest;
import sistemapedidos.dto.PedidoItensRequest;
import sistemapedidos.dto.PedidoResponse;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.PedidoServiceInterface;
import sistemapedidos.model.Pedido;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoServiceInterface pedidoService;

    public PedidoController(PedidoServiceInterface pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoCreateRequest request) {
        Pedido pedido = pedidoService.criarPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.from(pedido));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable UUID id) {
        return PedidoResponse.from(pedidoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public PedidoResponse substituirItens(@PathVariable UUID id, @RequestBody @Valid PedidoItensRequest request) {
        return PedidoResponse.from(pedidoService.substituirItens(id, somarQuantidadesPorProduto(request.itens())));
    }

    @PostMapping("/{id}/pagar")
    public PedidoResponse pagar(@PathVariable UUID id) {
        return PedidoResponse.from(pedidoService.pagar(id));
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable UUID id) {
        return PedidoResponse.from(pedidoService.cancelar(id));
    }

    private static Map<UUID, Integer> somarQuantidadesPorProduto(List<PedidoItemRequest> itens) {
        if (itens == null) {
            return new HashMap<>();
        }
        return itens.stream()
                .map(item -> {
                    if (item == null || item.produtoId() == null) {
                        throw new RegraNegocioException("Produto é obrigatório.");
                    }
                    return item;
                })
                .collect(Collectors.toMap(
                        PedidoItemRequest::produtoId,
                        PedidoItemRequest::quantidade,
                        Integer::sum
                ));
    }
}
