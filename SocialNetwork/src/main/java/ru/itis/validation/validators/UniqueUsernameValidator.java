package ru.itis.validation.validators;

import lombok.RequiredArgsConstructor;
import ru.itis.services.users.UsersService;
import ru.itis.validation.constraints.UniqueUsername;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, Object> {
    private final UsersService usersService;

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return !usersService.isUsernameExists((String) o);
    }
}
