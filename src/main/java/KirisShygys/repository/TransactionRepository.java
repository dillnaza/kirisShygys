package KirisShygys.repository;

import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_User(User user);
}
