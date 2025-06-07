package KirisShygys.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "user_fire_log")
@IdClass(UserFireLogId.class)
public class UserFireLog {

    @Id
    private Long userId;

    @Id
    private LocalDate fireDate;

    public UserFireLog() {
    }

    public UserFireLog(Long userId, LocalDate fireDate) {
        this.userId = userId;
        this.fireDate = fireDate;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public LocalDate getFireDate() {
        return fireDate;
    }
    public void setFireDate(LocalDate fireDate) {
        this.fireDate = fireDate;
    }
}