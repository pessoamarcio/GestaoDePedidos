package sistemapedidos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deveValidarCamposObrigatorios() {
        ClienteCreateRequest request = new ClienteCreateRequest("", "email-inválido", null);

        Set<ConstraintViolation<ClienteCreateRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }
}
