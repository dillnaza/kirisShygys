package KirisShygys.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class UserFireLogId implements Serializable {

    private Long userId;
    private LocalDate fireDate;

    public UserFireLogId() {
    }

    public UserFireLogId(Long userId, LocalDate fireDate) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFireLogId)) return false;
        UserFireLogId that = (UserFireLogId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(fireDate, that.fireDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fireDate);
    }
}
