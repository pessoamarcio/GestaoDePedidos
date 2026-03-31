package sistemapedidos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoItensRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deveValidarListaDeItensObrigatoria() {
        PedidoItensRequest request = new PedidoItensRequest(List.of());

        Set<ConstraintViolation<PedidoItensRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
    }
}
