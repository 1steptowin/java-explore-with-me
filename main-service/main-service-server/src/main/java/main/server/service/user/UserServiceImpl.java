package main.server.service.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.user.NewUserRequest;
import main.server.dto.user.UserDto;
import main.server.exception.user.UserNotFoundException;
import main.server.mapper.user.UserMapper;
import main.server.repo.user.UserRepo;
import main.server.service.EWMPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest user) {
        return UserMapper.mapModelToDto(userRepo.save(UserMapper.mapDtoToModel(user)));
    }

    @Override
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        return ids == null ? getAllUsers(new EWMPageRequest(from, size)) : getUsersByIds(ids, new EWMPageRequest(from, size));
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        checkIfUserNotExists(userId);
        userRepo.deleteById(userId);
    }

    private void checkIfUserNotExists(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("User %d not found", userId);
        }
    }

    private List<UserDto> getUsersByIds(Long[] ids, Pageable request) {
        return userRepo.findAllByUserIdIn(ids, request).getContent().stream()
                .map(UserMapper::mapModelToDto).collect(Collectors.toList());
    }

    private List<UserDto> getAllUsers(Pageable request) {
        return userRepo.findAll(request).getContent().stream()
                .map(UserMapper::mapModelToDto).collect(Collectors.toList());
    }
}
