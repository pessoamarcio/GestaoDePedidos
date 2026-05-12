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

import java.util.List;
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
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "12345678901", "maria@email.com");
        Cliente salvo = new Cliente(request.nome(), request.email(), request.cpf(), StatusCliente.ATIVO);
        when(clienteRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(clienteRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(clienteRepository.save(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(salvo);

        Cliente resultado = clienteService.cadastrar(request);

        assertSame(salvo, resultado);
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        assertEquals(request.nome(), captor.getValue().getNome());
        assertEquals(request.email(), captor.getValue().getEmail());
        assertEquals(request.cpf(), captor.getValue().getCpf());
        assertEquals(StatusCliente.ATIVO, captor.getValue().getStatus());
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoEmailJaExiste() {
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "12345678901", "maria@email.com");
        when(clienteRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThrows(RegraNegocioException.class,
                () -> clienteService.cadastrar(request));
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoCpfJaExiste() {
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "12345678901", "maria@email.com");
        when(clienteRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(clienteRepository.existsByCpf(request.cpf())).thenReturn(true);

        assertThrows(RegraNegocioException.class,
                () -> clienteService.cadastrar(request));
    }

    @Test
    void buscarPorIdDeveRetornarClienteQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", StatusCliente.ATIVO);
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

    @Test
    void buscarPorCpfOuNomeDeveBuscarPorCpfQuandoInformado() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", StatusCliente.ATIVO);
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));

        List<Cliente> resultado = clienteService.buscarPorCpfOuNome("12345678901", null);

        assertEquals(1, resultado.size());
        assertSame(cliente, resultado.get(0));
    }

    @Test
    void buscarPorCpfOuNomeDeveBuscarPorNomeQuandoCpfNaoInformado() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", StatusCliente.ATIVO);
        when(clienteRepository.findByNomeContainingIgnoreCase("Maria")).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.buscarPorCpfOuNome(null, "Maria");

        assertEquals(1, resultado.size());
        assertSame(cliente, resultado.get(0));
    }
}
