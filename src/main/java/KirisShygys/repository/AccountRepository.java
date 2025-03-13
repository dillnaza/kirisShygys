package KirisShygys.repository;

import KirisShygys.entity.Account;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser(@Param("user") User user);

    Optional<Account> findByIdAndUser(Long id, User user);
}
