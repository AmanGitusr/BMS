package meow.at.bms.service;

import meow.at.bms.dto.UserDto;
import meow.at.bms.entity.User;
import meow.at.bms.exception.ResourceNotFoundException;
import meow.at.bms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto);
        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + id));

        return mapToUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        User updatedUser = userRepository.save(user);

        return mapToUserDto(updatedUser);
    }

    private User mapToEntity(UserDto userDto) {
        User user = new User();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }
}
