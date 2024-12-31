package KirisShygys.service;

import KirisShygys.dto.UserDTO;
import KirisShygys.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDto);
    UserDTO updateUser(Long id, UserDTO userDto);
    void deleteUser(Long id);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
}
