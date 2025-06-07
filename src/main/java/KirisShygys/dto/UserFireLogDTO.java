package KirisShygys.dto;

import java.time.LocalDate;

public class UserFireLogDTO {
    private Long userId;
    private LocalDate fireDate;

    public UserFireLogDTO() {
    }

    public UserFireLogDTO(Long userId, LocalDate fireDate) {
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