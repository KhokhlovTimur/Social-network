package ru.itis.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.itis.exceptions.NoAccessException;
import ru.itis.models.User;
import ru.itis.security.utils.JwtUtil;
import ru.itis.security.utils.RequestParsingUtil;
import ru.itis.services.users.UsersService;

@Aspect
@Component
@RequiredArgsConstructor
public class AspectsService {
    private final RequestParsingUtil requestParsingUtil;
    private final UsersService usersService;

    @Around(value = "@annotation(ru.itis.annotations.TokenValid) ")
    public Object checkUserRights(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Long id = (Long) args[0];
        String token = (String) args[args.length - 1];
        String username = requestParsingUtil.getDataFromToken(token).get("username");

        User user = usersService.findById(id);

        if (!username.equals(user.getUsername())) {
            throw new NoAccessException("No access to the resource");
        }

        return joinPoint.proceed();
    }

}
