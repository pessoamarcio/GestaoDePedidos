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
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.dto.PedidoResponse;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.PedidoServiceInterface;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.enums.StatusPedido;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoCreateRequest request) {
        Pedido pedido = pedidoService.criarPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.from(pedido));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public PedidoResponse buscarPorId(@PathVariable UUID id) {
        return PedidoResponse.from(pedidoService.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<PedidoResponse> buscarPorCpfCliente(@RequestParam String cpf) {
        return pedidoService.buscarPorCpfCliente(cpf).stream()
                .map(PedidoResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public PedidoResponse atualizarStatus(
            @PathVariable UUID id,
            @Parameter(
                    //description = "Available values: PAGO, CANCELADO",
                    schema = @Schema(allowableValues = {"PAGO", "CANCELADO"})
            )
            @RequestParam StatusPedido status
    ) {
        if (status != StatusPedido.PAGO && status != StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Status inválido para atualização. Use PAGO ou CANCELADO.");
        }

        return PedidoResponse.from(pedidoService.atualizarStatus(id, status));
    }
}
