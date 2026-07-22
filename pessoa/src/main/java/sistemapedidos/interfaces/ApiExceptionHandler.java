package sistemapedidos.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.utils.HttpMessageErrorTranslator;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

	private final HttpMessageErrorTranslator httpMessageErrorTranslator = new HttpMessageErrorTranslator();

	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<ApiErrorResponse> handleNaoEncontrado(NaoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler({RegraNegocioException.class, IllegalStateException.class})
	public ResponseEntity<ApiErrorResponse> handleRegraNegocio(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
		String mensagem = ex.getReason() != null ? ex.getReason() : "Erro na requisição.";
		return ResponseEntity.status(ex.getStatusCode()).body(new ApiErrorResponse(mensagem));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiValidationErrorResponse> handleValidacao(MethodArgumentNotValidException ex) {
		List<ApiValidationErrorResponse.CampoInvalido> campos = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toCampoInvalido)
				.toList();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiValidationErrorResponse("Requisição inválida.", campos));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String mensagem = httpMessageErrorTranslator.traduzir(ex);
		if (mensagem != null) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body(new ApiErrorResponse(mensagem));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiErrorResponse("Corpo da requisição inválido."));
	}

	private ApiValidationErrorResponse.CampoInvalido toCampoInvalido(FieldError fieldError) {
		return new ApiValidationErrorResponse.CampoInvalido(fieldError.getField(), fieldError.getDefaultMessage());
	}

	public record ApiErrorResponse(String mensagem) {
	}

	public record ApiValidationErrorResponse(String mensagem, List<CampoInvalido> campos) {
		public record CampoInvalido(String campo, String mensagem) {
		}
	}
}
