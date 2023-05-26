package ru.itis.validation.constraints;

import ru.itis.validation.validators.UniqueGroupNameValidator;
import ru.itis.validation.validators.UniqueUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {

    String message() default "{username.not-unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
