package KirisShygys.repository;

import KirisShygys.entity.Balance;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByUser(User user);
}
