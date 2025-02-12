package KirisShygys.service;

import KirisShygys.dto.UserDTO;
import KirisShygys.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    void deleteUser(Long id);
    Optional<User> findByEmail(String email);
    User saveUser(User user);
}
