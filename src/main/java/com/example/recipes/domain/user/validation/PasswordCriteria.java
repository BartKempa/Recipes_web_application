package com.example.recipes.domain.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordCriteriaValidator.class)
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface PasswordCriteria {
    String message() default "Hasło musi mieć długość co najmniej 8 znaków, zawierać co najmniej jedną małą literę, zawierać co najmniej  jedną wielką literę, zawierać co najmniej  jedną cyfrę oraz zawierać co najmniej  jeden znak specjalny. ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}