package ru.ylab.tasks.task3.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task3.dto.response.user.RegisterResponse;
import ru.ylab.tasks.task3.model.User;

@Mapper
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterRequest dto);

    default RegisterResponse toResponse(User user) {
        return new RegisterResponse(
                user.getLogin(),
                user.getRole().name()
        );
    }

}
