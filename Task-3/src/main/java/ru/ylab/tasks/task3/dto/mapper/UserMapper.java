package ru.ylab.tasks.task3.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task3.dto.response.user.RegisterResponse;
import ru.ylab.tasks.task3.model.User;

/**
 * Маппер для преобразования между сущностью User и DTO.
 * Использует MapStruct для автоматического преобразования объектов.
 */
@Mapper
public interface UserMapper {

    /**
     * Преобразует DTO запроса на регистрацию в сущность User.
     * Игнорирует поле id при преобразовании.
     * @param dto DTO запроса на регистрацию
     * @return сущность User
     */
    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterRequest dto);

    /**
     * Преобразует сущность User в DTO ответа на регистрацию.
     * @param user сущность User
     * @return DTO ответа на регистрацию
     */
    default RegisterResponse toResponse(User user) {
        return new RegisterResponse(
                user.getLogin(),
                user.getRole().name()
        );
    }

}
