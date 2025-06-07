package KirisShygys.service;

public interface StreakService {
    boolean hasTodayFire(String token);
    int getStreakCount(String token);
    void lightFireManually(String token);
    void lightFireAutomatically(Long userId);
}