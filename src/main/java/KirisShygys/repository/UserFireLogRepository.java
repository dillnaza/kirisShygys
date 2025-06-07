package KirisShygys.repository;

import KirisShygys.entity.UserFireLog;
import KirisShygys.entity.UserFireLogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserFireLogRepository extends JpaRepository<UserFireLog, UserFireLogId> {
    boolean existsByUserIdAndFireDate(Long userId, LocalDate date);
}