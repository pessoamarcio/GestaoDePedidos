package sistemapedidos.dto;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.model.Cliente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteResponseTest {

    @Test
    void fromDeveMapearCliente() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901");
        UUID id = UUID.randomUUID();
        TestReflectionUtils.setField(cliente, "id", id);

        ClienteResponse response = ClienteResponse.from(cliente);

        assertEquals(id, response.id());
        assertEquals("12345678901", response.cpf());
        assertEquals("Maria", response.nome());
        assertEquals("maria@email.com", response.email());
    }
}
