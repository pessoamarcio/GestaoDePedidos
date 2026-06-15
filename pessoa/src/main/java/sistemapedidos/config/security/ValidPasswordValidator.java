package sistemapedidos.config.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		String err = PasswordPolicy.validate(value, "password");
		if (err == null) {
			return true;
		}

		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(err).addConstraintViolation();
		return false;
	}
}

