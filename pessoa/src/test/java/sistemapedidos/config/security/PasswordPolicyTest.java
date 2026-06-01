package sistemapedidos.config.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyTest {

	@Test
	void acceptsValidPassword() {
		assertDoesNotThrow(() -> PasswordPolicy.validateOrThrow("Mr@@1984", "password"));
	}

	@Test
	void rejectsTooShort() {
		assertThrows(IllegalStateException.class, () -> PasswordPolicy.validateOrThrow("A@@1234", "password"));
	}

	@Test
	void rejectsNotEnoughLetters() {
		assertThrows(IllegalStateException.class, () -> PasswordPolicy.validateOrThrow("12@@3456", "password"));
	}

	@Test
	void rejectsNotEnoughSpecials() {
		assertThrows(IllegalStateException.class, () -> PasswordPolicy.validateOrThrow("Abcdef12", "password"));
	}
}

