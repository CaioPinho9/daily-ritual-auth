package com.caio.pinho.auth.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "email is required")
@Email(message = "email must be valid")
public @interface ValidEmail {

	String message() default "email must be valid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
