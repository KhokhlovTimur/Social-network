package ru.itis.validation.validators;

import lombok.RequiredArgsConstructor;
import ru.itis.services.groups.GroupsService;
import ru.itis.validation.constraints.UniqueGroupName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueGroupNameValidator implements ConstraintValidator<UniqueGroupName, Object> {
    private final GroupsService groupsService;
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return !groupsService.isNameOccupied((String) o);
    }
}
