package sistemapedidos.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Cliente;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteRepositoryTest {

    @Test
    void deveSerInterfaceJpaRepositoryDeCliente() {
        assertTrue(ClienteRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(ClienteRepository.class));

        ParameterizedType repositoryType = (ParameterizedType) ClienteRepository.class.getGenericInterfaces()[0];
        assertEquals(Cliente.class, repositoryType.getActualTypeArguments()[0]);
        assertEquals(UUID.class, repositoryType.getActualTypeArguments()[1]);
    }
}
