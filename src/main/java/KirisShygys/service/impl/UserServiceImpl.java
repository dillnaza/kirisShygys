package KirisShygys.service.impl;

import KirisShygys.dto.UserDTO;
import KirisShygys.entity.User;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDto) {
        User user = convertToEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Хэшируем пароль
        userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userRepository.save(existingUser);
        return convertToDTO(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Преобразование из User в UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getUserId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    // Преобразование из UserDTO в User
    private User convertToEntity(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }
}
