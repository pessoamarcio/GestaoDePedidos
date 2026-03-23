package sistemapedidos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiConfigTest {

    @Test
    void openApiConfigDevePossuirAnotacoesEsperadas() {
        Configuration configuration = OpenApiConfig.class.getAnnotation(Configuration.class);
        OpenAPIDefinition definition = OpenApiConfig.class.getAnnotation(OpenAPIDefinition.class);

        assertNotNull(configuration);
        assertNotNull(definition);
        Info info = definition.info();
        assertEquals("Sistema de Gestao de Pedidos (Pessoa)", info.title());
        assertEquals("v1", info.version());
        assertTrue(info.description().contains("API REST"));
    }
}
