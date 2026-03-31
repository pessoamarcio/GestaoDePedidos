package sistemapedidos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "Sistema de Gestao de Pedidos",
				version = "v1",
				description = "API REST para gestao de clientes, produtos e pedidos.",
				license = @License(name = "Proprietary")
		)
)
public class OpenApiConfig {
}
