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
        ClienteCreateRequest request = new ClienteCreateRequest("", "", "email-invalido", null);

        Set<ConstraintViolation<ClienteCreateRequest>> violations = validator.validate(request);

        assertEquals(5, violations.size());
    }

    @Test
    void deveValidarCpfComOnzeNumeros() {
        ClienteCreateRequest request = new ClienteCreateRequest("Maria", "12345", "maria@email.com", null);

        Set<ConstraintViolation<ClienteCreateRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("CPF deve conter 11")));
    }
}
