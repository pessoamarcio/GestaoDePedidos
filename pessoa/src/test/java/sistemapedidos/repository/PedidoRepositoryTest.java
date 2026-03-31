package sistemapedidos.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.enums.StatusCliente;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryTest {

    @Mock
    private PedidoRepositoryJpa pedidoRepositoryJpa;

    @InjectMocks
    private PedidoRepository pedidoRepository;

    @Test
    void saveDeveDelegarParaJpa() {
        Pedido pedido = new Pedido(new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO));
        when(pedidoRepositoryJpa.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoRepository.save(pedido);

        assertSame(pedido, resultado);
    }

    @Test
    void findByIdDeveDelegarParaJpa() {
        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido(new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO));
        when(pedidoRepositoryJpa.findById(id)).thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoRepository.findById(id);

        assertSame(pedido, resultado.orElseThrow());
    }
}
