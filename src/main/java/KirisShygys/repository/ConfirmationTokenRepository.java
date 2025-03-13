package KirisShygys.repository;

import KirisShygys.entity.ConfirmationToken;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
    void deleteByUser(User user);
}
