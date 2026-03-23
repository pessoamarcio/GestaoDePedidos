package sistemapedidos.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamUtilsTest {

    @Test
    void unionKeysDeveUnirChavesSemDuplicidade() {
        UUID left = UUID.randomUUID();
        UUID shared = UUID.randomUUID();
        UUID right = UUID.randomUUID();

        Set<UUID> resultado = StreamUtils.unionKeys(Map.of(left, 1, shared, 2), Map.of(shared, 3, right, 4));

        assertEquals(Set.of(left, shared, right), resultado);
    }

    @Test
    void construtorPrivadoDeveSerAcessivelPorReflexao() throws Exception {
        Constructor<StreamUtils> constructor = StreamUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        constructor.newInstance();
    }
}
