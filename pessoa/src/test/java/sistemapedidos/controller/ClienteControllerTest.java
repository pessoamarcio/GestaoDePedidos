package sistemapedidos.controller;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.dto.EnderecoRequest;
import sistemapedidos.dto.ClienteResponse;
import sistemapedidos.interfaces.ClienteServiceInterface;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.Endereco;
import sistemapedidos.model.enums.StatusCliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteServiceInterface clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void cadastrarDeveRetornarCreatedComClienteResponse() {
        EnderecoRequest endereco = new EnderecoRequest("Rua A", "10", null, "Centro", "São Paulo", "SP", "01001000");
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "12345678901", "maria@email.com", endereco);
        Cliente cliente = new Cliente(request.nome(), request.email(), request.cpf(), new Endereco(endereco.logradouro(), endereco.numero(), endereco.complemento(), endereco.bairro(), endereco.cidade(), endereco.estado(), endereco.cep()));
        UUID id = UUID.randomUUID();
        TestReflectionUtils.setField(cliente, "id", id);
        when(clienteService.cadastrar(request)).thenReturn(cliente);

        ResponseEntity<ClienteResponse> response = clienteController.cadastrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(ClienteResponse.class, response.getBody());
        assertEquals(id, response.getBody().id());
        assertEquals("12345678901", response.getBody().cpf());
        assertEquals(StatusCliente.ATIVO, response.getBody().status());
        assertEquals("Rua A", response.getBody().endereco().logradouro());
    }

    @Test
    void buscarPorIdDeveRetornarClienteResponse() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", new Endereco("Rua A", "10", null, "Centro", "São Paulo", "SP", "01001000"));
        TestReflectionUtils.setField(cliente, "id", id);
        when(clienteService.buscarPorId(id)).thenReturn(cliente);

        ClienteResponse response = clienteController.buscarPorId(id);

        assertEquals(id, response.id());
        assertEquals("Maria", response.nome());
        assertEquals("12345678901", response.cpf());
        assertEquals(StatusCliente.ATIVO, response.status());
        assertEquals("São Paulo", response.endereco().cidade());
    }

    @Test
    void buscarPorCpfOuNomeDeveRetornarListaDeClientes() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", new Endereco("Rua A", "10", null, "Centro", "São Paulo", "SP", "01001000"));
        UUID id = UUID.randomUUID();
        TestReflectionUtils.setField(cliente, "id", id);
        when(clienteService.buscarPorCpfOuNome(null, "Maria")).thenReturn(List.of(cliente));

        List<ClienteResponse> response = clienteController.buscarPorCpfOuNome(null, "Maria");

        assertEquals(1, response.size());
        assertEquals(id, response.get(0).id());
        assertEquals("Maria", response.get(0).nome());
    }
}
