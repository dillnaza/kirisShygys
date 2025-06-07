package KirisShygys.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_streak")
public class UserStreak {

    @Id
    private Long userId;

    @Column(nullable = false)
    private int streakCount;

    @Column(nullable = false)
    private LocalDate lastActiveDate;

    public UserStreak() {
    }

    public UserStreak(Long userId, int streakCount, LocalDate lastActiveDate) {
        this.userId = userId;
        this.streakCount = streakCount;
        this.lastActiveDate = lastActiveDate;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public int getStreakCount() {
        return streakCount;
    }
    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }
    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }
    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
}