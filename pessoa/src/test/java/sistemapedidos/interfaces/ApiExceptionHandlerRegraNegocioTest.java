package sistemapedidos.interfaces;

import sistemapedidos.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerRegraNegocioTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleRegraNegocioDeveRetornarBadRequest() {
        ResponseEntity<ApiExceptionHandler.ApiErrorResponse> response =
                handler.handleRegraNegocio(new RegraNegocioException("regra"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("regra", response.getBody().mensagem());
    }

    @Test
    void handleIllegalStateDeveRetornarBadRequest() {
        ResponseEntity<ApiExceptionHandler.ApiErrorResponse> response =
                handler.handleRegraNegocio(new IllegalStateException("ilegal"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ilegal", response.getBody().mensagem());
    }
}
