package sistemapedidos.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Pedido;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoRepositoryTest {

    @Test
    void deveSerInterfaceJpaRepositoryDePedido() {
        assertTrue(PedidoRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(PedidoRepository.class));

        ParameterizedType repositoryType = (ParameterizedType) PedidoRepository.class.getGenericInterfaces()[0];
        assertEquals(Pedido.class, repositoryType.getActualTypeArguments()[0]);
        assertEquals(UUID.class, repositoryType.getActualTypeArguments()[1]);
    }
}
