package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.UserRequestDto;
import ru.practicum.ewm.dto.user.UserResponseDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getUsers(List<Long> ids, Pageable page) {
        if (ids != null && ids.size() > 0) {
            return userRepository.findAllByIds(ids, page).stream()
                    .map(UserMapper::toUserResponseDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAll(page).toList().stream()
                .map(UserMapper::toUserResponseDto)
                .sorted(Comparator.comparing(UserResponseDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto saveUser(UserRequestDto user) {
        if (userRepository.findByName(user.getName()) != null) {
            throw new ConflictException("User name: " + user.getName() + " is already taken");
        }
        return UserMapper.toUserResponseDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.delete(user);
    }
}
