package KirisShygys.service;

import KirisShygys.entity.Balance;
import KirisShygys.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    public List<Balance> getBalancesByUserId(Long userId) {
        return balanceRepository.findByUser_UserId(userId);
    }
}

