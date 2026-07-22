package sistemapedidos.utils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpMessageErrorTranslator {

	private static final Pattern ENUM_INVALIDO_PATTERN = Pattern.compile(
			"Cannot deserialize value of type `([^`]+)` from String \\\"([^\\\"]+)\\\":.*Enum class: \\[([^\\]]+)\\]"
	);

	public String traduzir(HttpMessageNotReadableException ex) {
		InvalidFormatException invalidFormatException = findCause(ex, InvalidFormatException.class);
		if (invalidFormatException != null
				&& invalidFormatException.getTargetType() != null
				&& invalidFormatException.getTargetType().isEnum()) {
			return montarMensagemEnumInvalido(
					extrairCampo(invalidFormatException),
					Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
							.map(String::valueOf)
							.toList()
			);
		}

		JsonMappingException jsonMappingException = findCause(ex, JsonMappingException.class);
		if (jsonMappingException != null) {
			String mensagem = extrairMensagemEnumInvalido(jsonMappingException);
			if (mensagem != null) {
				return mensagem;
			}
		}

		Throwable causaMaisEspecifica = ex.getMostSpecificCause();
		if (causaMaisEspecifica instanceof JsonMappingException jsonMappingMaisEspecifica) {
			String mensagem = extrairMensagemEnumInvalido(jsonMappingMaisEspecifica);
			if (mensagem != null) {
				return mensagem;
			}
		}

		return extrairMensagemEnumInvalido(ex.getMessage(), "status");
	}

	private String extrairMensagemEnumInvalido(JsonMappingException exception) {
		String campo = exception.getPath().isEmpty() ? "valor" : exception.getPath().getFirst().getFieldName();
		return extrairMensagemEnumInvalido(exception.getOriginalMessage(), campo);
	}

	private String extrairMensagemEnumInvalido(String mensagemOriginal, String campoPadrao) {
		if (mensagemOriginal == null) {
			return null;
		}
		Matcher matcher = ENUM_INVALIDO_PATTERN.matcher(mensagemOriginal);
		if (!matcher.find()) {
			return null;
		}
		List<String> valoresValidos = Arrays.stream(matcher.group(3).split(","))
				.map(String::trim)
				.toList();
		return montarMensagemEnumInvalido(campoPadrao, valoresValidos);
	}

	private String extrairCampo(InvalidFormatException invalidFormatException) {
		return invalidFormatException.getPath().isEmpty()
				? "valor"
				: invalidFormatException.getPath().getFirst().getFieldName();
	}

	private String montarMensagemEnumInvalido(String campo, List<String> valoresValidos) {
		if ("status".equals(campo)) {
			return "Atualização de status inválida. Status válidos: PAGO, CANCELADO, AGUARDANDO_PAGAMENTO.";
		}
		String valores = valoresValidos.stream().collect(Collectors.joining(", "));
		return campo + " inválido. Valores válidos: " + valores + ".";
	}

	private <T extends Throwable> T findCause(Throwable throwable, Class<T> targetType) {
		Throwable atual = throwable;
		while (atual != null) {
			if (targetType.isInstance(atual)) {
				return targetType.cast(atual);
			}
			atual = atual.getCause();
		}
		return null;
	}
}
