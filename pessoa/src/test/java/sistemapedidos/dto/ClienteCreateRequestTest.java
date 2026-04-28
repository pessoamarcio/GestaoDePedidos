package sistemapedidos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deveValidarCamposObrigatorios() {
        ClienteCreateRequest request = new ClienteCreateRequest("", "", "email-invalido");

        Set<ConstraintViolation<ClienteCreateRequest>> violations = validator.validate(request);

        // CPF vazio viola @NotBlank e @Pattern, por isso o total aqui fica 4.
        assertEquals(4, violations.size());
    }

    @Test
    void deveValidarCpfComOnzeNumeros() {
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "12345", "maria@email.com");

        Set<ConstraintViolation<ClienteCreateRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("CPF deve conter 11")));
    }
}

