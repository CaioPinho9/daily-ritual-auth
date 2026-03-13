package com.caio.pinho.auth.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "password is required")
@Size(min = 8, message = "password must have at least 8 characters")
@Pattern(
		regexp = "^(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
		message = "password must contain at least one number and one symbol"
)
public @interface ValidPassword {

	String message() default "password must have at least 8 characters";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
