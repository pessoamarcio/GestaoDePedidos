package sistemapedidos.repository;

import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryTest {

    @Mock
    private ClienteRepositoryJpa clienteRepositoryJpa;

    @InjectMocks
    private ClienteRepository clienteRepository;

    @Test
    void saveDeveDelegarParaJpa() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO);
        when(clienteRepositoryJpa.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteRepository.save(cliente);

        assertSame(cliente, resultado);
    }

    @Test
    void findByIdDeveDelegarParaJpa() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO);
        when(clienteRepositoryJpa.findById(id)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteRepository.findById(id);

        assertSame(cliente, resultado.orElseThrow());
    }

    @Test
    void existsByEmailIgnoreCaseDeveDelegarParaJpa() {
        when(clienteRepositoryJpa.existsByEmailIgnoreCase("maria@email.com")).thenReturn(true);

        boolean resultado = clienteRepository.existsByEmailIgnoreCase("maria@email.com");

        assertTrue(resultado);
    }
}
