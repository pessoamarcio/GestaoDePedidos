package sistemapedidos.service;

import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void cadastrarDeveSalvarClienteQuandoEmailNaoExiste() {
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "maria@email.com", StatusCliente.ATIVO);
        Cliente salvo = new Cliente(request.nome(), request.email(), request.status());
        when(clienteRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(clienteRepository.save(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(salvo);

        Cliente resultado = clienteService.cadastrar(request);

        assertSame(salvo, resultado);
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        assertEquals(request.nome(), captor.getValue().getNome());
        assertEquals(request.email(), captor.getValue().getEmail());
        assertEquals(request.status(), captor.getValue().getStatus());
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoEmailJaExiste() {
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "maria@email.com", StatusCliente.ATIVO);
        when(clienteRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThrows(RegraNegocioException.class,
                () -> clienteService.cadastrar(request));
    }

    @Test
    void buscarPorIdDeveRetornarClienteQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO);
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId(id);

        assertSame(cliente, resultado);
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> clienteService.buscarPorId(id));
    }
}
