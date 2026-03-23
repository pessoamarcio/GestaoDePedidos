package sistemapedidos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProdutoCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deveValidarCamposObrigatorios() {
        ProdutoCreateRequest request = new ProdutoCreateRequest("", null, -1, null);

        Set<ConstraintViolation<ProdutoCreateRequest>> violations = validator.validate(request);

        assertEquals(3, violations.size());
    }
}
