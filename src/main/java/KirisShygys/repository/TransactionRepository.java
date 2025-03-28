package KirisShygys.repository;

import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAndDatetimeBetween(User user, LocalDateTime dateFrom, LocalDateTime dateTo);
    List<Transaction> findByUser(User user);
}