package sistemapedidos.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import sistemapedidos.model.Produto;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoRepositoryTest {

    @Test
    void deveSerInterfaceJpaRepositoryDeProduto() {
        assertTrue(ProdutoRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(ProdutoRepository.class));

        ParameterizedType repositoryType = (ParameterizedType) ProdutoRepository.class.getGenericInterfaces()[0];
        assertEquals(Produto.class, repositoryType.getActualTypeArguments()[0]);
        assertEquals(UUID.class, repositoryType.getActualTypeArguments()[1]);
    }
}
