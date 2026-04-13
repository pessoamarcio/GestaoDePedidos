package sistemapedidos.model;

import sistemapedidos.model.enums.StatusCliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteTest {

    @Test
    void construtorDeveAplicarStatusPadraoQuandoNulo() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", null);

        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
    }

    @Test
    void construtorSemStatusDeveCriarClienteAtivo() {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901");

        assertEquals("Maria", cliente.getNome());
        assertEquals("maria@email.com", cliente.getEmail());
        assertEquals("12345678901", cliente.getCpf());
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
    }
}
