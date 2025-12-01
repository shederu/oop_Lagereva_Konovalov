package ru.ssau.tk._shederu_._lab1_.service;

import org.springframework.stereotype.Service;
import ru.ssau.tk._shederu_._lab1_.dto.UserDto;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, UserDto> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<UserDto> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public UserDto getUserById(Long id) {
        return users.get(id);
    }

    public UserDto createUser(UserDto userDto) {
        Long id = idCounter.getAndIncrement();
        userDto.setId(id);
        users.put(id, userDto);
        return userDto;
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        if (!users.containsKey(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userDto.setId(id);
        users.put(id, userDto);
        return userDto;
    }

    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        users.remove(id);
    }

    public UserDto findByLogin(String login) {
        return users.values().stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }
}