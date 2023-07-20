package main.server.service.user;

import main.server.dto.user.NewUserRequest;
import main.server.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest user);

    List<UserDto> getUsers(Long[] ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}
