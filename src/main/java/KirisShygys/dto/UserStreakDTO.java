package KirisShygys.dto;

import java.time.LocalDate;

public class UserStreakDTO {
    private Long userId;
    private int streakCount;
    private LocalDate lastActiveDate;

    public UserStreakDTO() {
    }

    public UserStreakDTO(Long userId, int streakCount, LocalDate lastActiveDate) {
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
