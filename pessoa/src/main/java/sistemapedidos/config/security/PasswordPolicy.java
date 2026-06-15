package sistemapedidos.config.security;

/**
 * Regra mínima de senha:
 * - pelo menos 8 caracteres
 * - pelo menos 2 letras
 * - pelo menos 2 caracteres especiais (não letras nem dígitos)
 */

public final class PasswordPolicy {

	public static final int MIN_LENGTH = 8;
	public static final int MIN_LETTERS = 2;
	public static final int MIN_SPECIALS = 2;

	private PasswordPolicy() {
	}

	public static void validateOrThrow(String password, String fieldName) {
		String err = validate(password, fieldName);
		if (err != null) {
			throw new IllegalStateException(err);
		}
	}

	static String validate(String password, String fieldName) {
		if (password == null || password.isBlank()) {
			return fieldName + " não pode ser vazio.";
		}

		int letters = 0;
		int specials = 0;

		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (Character.isLetter(c)) {
				letters++;
			} else if (!Character.isDigit(c)) {
				// Tudo que não for letra nem digito conta como "especial"
				specials++;
			}
		}

		if (password.length() < MIN_LENGTH || letters < MIN_LETTERS || specials < MIN_SPECIALS) {
			return fieldName + " deve ter no mínimo " + MIN_LENGTH + " caracteres, com pelo menos "
					+ MIN_LETTERS + " letras e " + MIN_SPECIALS + " caracteres especiais.";
		}

		return null;
	}
}

