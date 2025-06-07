package KirisShygys.service.impl;

import KirisShygys.entity.User;
import KirisShygys.entity.UserFireLog;
import KirisShygys.entity.UserStreak;
import KirisShygys.exception.FireAlreadyLitException;
import KirisShygys.repository.UserFireLogRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.repository.UserStreakRepository;
import KirisShygys.service.StreakService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class StreakServiceImpl extends BaseService implements StreakService {

    private final UserFireLogRepository fireLogRepository;
    private final UserStreakRepository streakRepository;
    private final UserRepository userRepository;

    public StreakServiceImpl(UserFireLogRepository fireLogRepository,
                             UserStreakRepository streakRepository,
                             JwtUtil jwtUtil, UserRepository userRepository) {
        super(userRepository, jwtUtil);
        this.fireLogRepository = fireLogRepository;
        this.streakRepository = streakRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean hasTodayFire(String token) {
        User user = getAuthenticatedUser(token);
        return fireLogRepository.existsByUserIdAndFireDate(user.getId(), LocalDate.now());
    }

    @Override
    public int getStreakCount(String token) {
        User user = getAuthenticatedUser(token);
        return streakRepository.findById(user.getId())
                .map(UserStreak::getStreakCount)
                .orElse(0);
    }

    @Override
    @Transactional
    public void lightFireManually(String token) {
        User user = getAuthenticatedUser(token);
        LocalDate today = LocalDate.now();
        if (fireLogRepository.existsByUserIdAndFireDate(user.getId(), today)) {
            throw new FireAlreadyLitException("You have already recorded today's activity.");
        }
        fireLogRepository.save(new UserFireLog(user.getId(), today));
        updateUserStreak(user.getId(), today);
    }

    @Override
    @Transactional
    public void lightFireAutomatically(Long userId) {
        LocalDate today = LocalDate.now();
        if (fireLogRepository.existsByUserIdAndFireDate(userId, today)) return;
        fireLogRepository.save(new UserFireLog(userId, today));
        updateUserStreak(userId, today);
    }

    private void updateUserStreak(Long userId, LocalDate today) {
        UserStreak streak = streakRepository.findById(userId).orElseGet(() -> {
            UserStreak s = new UserStreak();
            s.setUserId(userId);
            s.setStreakCount(0);
            s.setLastActiveDate(today.minusDays(1));
            return s;
        });
        if (streak.getLastActiveDate().isEqual(today)) return;

        if (streak.getLastActiveDate().isEqual(today.minusDays(1))) {
            streak.setStreakCount(streak.getStreakCount() + 1);
        } else {
            streak.setStreakCount(1);
        }
        streak.setLastActiveDate(today);
        streakRepository.save(streak);
    }
}