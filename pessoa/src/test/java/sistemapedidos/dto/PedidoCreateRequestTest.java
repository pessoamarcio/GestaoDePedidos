package sistemapedidos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deveValidarCamposObrigatorios() {
        PedidoCreateRequest request = new PedidoCreateRequest(null, List.of());

        Set<ConstraintViolation<PedidoCreateRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }
}
