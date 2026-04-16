package sistemapedidos.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.dto.PedidoResponse;
import sistemapedidos.dto.PedidoStatusUpdateRequest;
import sistemapedidos.interfaces.PedidoServiceInterface;
import sistemapedidos.model.Pedido;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public List<PedidoResponse> buscarPorCpfCliente(@RequestParam String cpf) {
        return pedidoService.buscarPorCpfCliente(cpf).stream()
                .map(PedidoResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizarStatus(@PathVariable UUID id, @RequestBody @Valid PedidoStatusUpdateRequest request) {
        return PedidoResponse.from(pedidoService.atualizarStatus(id, request.status()));
    }
}
