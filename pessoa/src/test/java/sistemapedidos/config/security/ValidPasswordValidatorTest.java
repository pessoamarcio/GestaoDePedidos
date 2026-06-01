package sistemapedidos.config.security;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidPasswordValidatorTest {

	private static Validator validator;
	private static AutoCloseable factory;

	private record Req(@ValidPassword String password) {
	}

	@BeforeAll
	static void setup() {
		var validatorFactory = Validation.buildDefaultValidatorFactory();
		factory = validatorFactory;
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void tearDown() throws Exception {
		if (factory != null) factory.close();
	}

	@Test
	void invalidPasswordProducesConstraintViolation() {
		var violations = validator.validate(new Req("admin"));
		assertFalse(violations.isEmpty());
	}

	@Test
	void validPasswordHasNoViolations() {
		var violations = validator.validate(new Req("Mr@@1984"));
		assertTrue(violations.isEmpty());
	}
}

