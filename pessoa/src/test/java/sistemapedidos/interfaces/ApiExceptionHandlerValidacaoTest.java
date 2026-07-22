package sistemapedidos.interfaces;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import sistemapedidos.model.enums.StatusPedido;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerValidacaoTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleValidacaoDeveRetornarCamposInvalidos() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyRequest(null), "dummyRequest");
        bindingResult.rejectValue("nome", "NotBlank", "nome obrigatorio");
        Method method = DummyController.class.getDeclaredMethod("dummy", DummyRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiExceptionHandler.ApiValidationErrorResponse> response = handler.handleValidacao(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida.", response.getBody().mensagem());
        assertEquals(1, response.getBody().campos().size());
        assertEquals("nome", response.getBody().campos().getFirst().campo());
        assertEquals("nome obrigatorio", response.getBody().campos().getFirst().mensagem());
    }

    @Test
    void handleHttpMessageNotReadableDeveRetornarMensagemComStatusValidos() {
        InvalidFormatException cause = InvalidFormatException.from(
                null,
                "status invalido",
                "FINALIZADO",
                StatusPedido.class
        );
        cause.prependPath(new DummyRequest(null), "status");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("json invalido", cause, null);

        ResponseEntity<ApiExceptionHandler.ApiErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "Atualização de status inválida. Status válidos: PAGO, CANCELADO, AGUARDANDO_PAGAMENTO.",
                response.getBody().mensagem()
        );
    }

    @Test
    void handleHttpMessageNotReadableDeveEncontrarInvalidFormatNaCadeiaDeCausas() {
        InvalidFormatException rootCause = InvalidFormatException.from(
                null,
                "status invalido",
                "QUASEPAGO",
                StatusPedido.class
        );
        rootCause.prependPath(new DummyRequest(null), "status");
        RuntimeException intermediateCause = new RuntimeException("erro intermediario", rootCause);
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("json invalido", intermediateCause, null);

        ResponseEntity<ApiExceptionHandler.ApiErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "Atualização de status inválida. Status válidos: PAGO, CANCELADO, AGUARDANDO_PAGAMENTO.",
                response.getBody().mensagem()
        );
    }

    @Test
    void handleHttpMessageNotReadableDeveUsarMensagemDaExcecaoQuandoNaoHouverCausaMapeavel() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "JSON parse error: Cannot deserialize value of type `sistemapedidos.model.enums.StatusPedido` " +
                        "from String \"QUASEPAGO\": not one of the values accepted for Enum class: " +
                        "[AGUARDANDO_PAGAMENTO, CANCELADO, PAGO]",
                inputMessage()
        );

        ResponseEntity<ApiExceptionHandler.ApiErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(
                "Atualização de status inválida. Status válidos: PAGO, CANCELADO, AGUARDANDO_PAGAMENTO.",
                response.getBody().mensagem()
        );
    }

    private static final class DummyController {
        @SuppressWarnings("unused")
        void dummy(DummyRequest request) {
        }
    }

    private HttpInputMessage inputMessage() {
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public HttpHeaders getHeaders() {
                return HttpHeaders.EMPTY;
            }
        };
    }

    private record DummyRequest(String nome) {
    }
}
