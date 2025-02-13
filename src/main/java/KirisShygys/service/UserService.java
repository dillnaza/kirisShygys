package KirisShygys.service;

import KirisShygys.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    User saveUser(User user);
    void deleteUser(Long id);
}
