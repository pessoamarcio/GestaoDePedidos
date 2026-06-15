package sistemapedidos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "Sistema de Gestao de Pedidos",
				version = "v1",
				description = "API REST para gestao de clientes, produtos e pedidos.",
				license = @License(name = "Proprietary")
		)
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
public class OpenApiConfig {

	@Bean
	public OpenApiCustomizer applyBearerAuthOnlyToApiPaths() {
		return openApi -> {
			if (openApi.getPaths() == null) return;

			openApi.getPaths().forEach((path, item) -> {
				if (path == null || item == null) return;
				if (!path.startsWith("/api/")) return;

				SecurityRequirement requirement = new SecurityRequirement().addList("bearerAuth");
				if (item.getGet() != null) item.getGet().addSecurityItem(requirement);
				if (item.getPost() != null) item.getPost().addSecurityItem(requirement);
				if (item.getPut() != null) item.getPut().addSecurityItem(requirement);
				if (item.getDelete() != null) item.getDelete().addSecurityItem(requirement);
				if (item.getPatch() != null) item.getPatch().addSecurityItem(requirement);
				if (item.getHead() != null) item.getHead().addSecurityItem(requirement);
				if (item.getOptions() != null) item.getOptions().addSecurityItem(requirement);
				if (item.getTrace() != null) item.getTrace().addSecurityItem(requirement);
			});
		};
	}
}
