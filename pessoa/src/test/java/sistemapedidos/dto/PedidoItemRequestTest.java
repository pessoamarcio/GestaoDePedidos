package sistemapedidos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoItemRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deveValidarProdutoEQuantidade() {
        PedidoItemRequest request = new PedidoItemRequest(null, 0);

        Set<ConstraintViolation<PedidoItemRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }
}
