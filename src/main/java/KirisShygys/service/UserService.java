package KirisShygys.service;

import KirisShygys.entity.User;

import java.util.Optional;

public interface UserService {
    void deleteUser(Long id);
    Optional<User> findByEmail(String email);
    User saveUser(User user);
}
