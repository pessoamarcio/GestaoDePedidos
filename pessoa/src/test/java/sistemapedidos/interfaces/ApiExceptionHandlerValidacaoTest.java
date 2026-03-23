package sistemapedidos.interfaces;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerValidacaoTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleValidacaoDeveRetornarCamposInvalidos() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyRequest(null), "dummyRequest");
        bindingResult.rejectValue("nome", "NotBlank", "nome obrigatório");
        Method method = DummyController.class.getDeclaredMethod("dummy", DummyRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiExceptionHandler.ApiValidationErrorResponse> response = handler.handleValidacao(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida.", response.getBody().mensagem());
        assertEquals(1, response.getBody().campos().size());
        assertEquals("nome", response.getBody().campos().getFirst().campo());
        assertEquals("nome obrigatório", response.getBody().campos().getFirst().mensagem());
    }

    private static final class DummyController {
        @SuppressWarnings("unused")
        void dummy(DummyRequest request) {
        }
    }

    private record DummyRequest(String nome) {
    }
}
