package sistemapedidos.controller;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.dto.ClienteResponse;
import sistemapedidos.interfaces.ClienteServiceInterface;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "maria@email.com", StatusCliente.ATIVO);
        Cliente cliente = new Cliente(request.nome(), request.email(), request.status());
        UUID id = UUID.randomUUID();
        TestReflectionUtils.setField(cliente, "id", id);
        when(clienteService.cadastrar(request.nome(), request.email(), request.status())).thenReturn(cliente);

        ResponseEntity<ClienteResponse> response = clienteController.cadastrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(ClienteResponse.class, response.getBody());
        assertEquals(id, response.getBody().id());
    }

    @Test
    void buscarPorIdDeveRetornarClienteResponse() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente("Maria", "maria@email.com", StatusCliente.ATIVO);
        TestReflectionUtils.setField(cliente, "id", id);
        when(clienteService.buscarPorId(id)).thenReturn(cliente);

        ClienteResponse response = clienteController.buscarPorId(id);

        assertEquals(id, response.id());
        assertEquals("Maria", response.nome());
    }
}
