package KirisShygys.service.impl;

import KirisShygys.entity.Balance;
import KirisShygys.entity.User;
import KirisShygys.repository.BalanceRepository;
import KirisShygys.service.BalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;

    public BalanceServiceImpl(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public BigDecimal getUserTotalBalance(User user) {
        return balanceRepository.findByUser(user).stream()
                .filter(balance -> balance.getBalanceType() == Balance.BalanceType.TOTAL)
                .map(Balance::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getUserIncome(User user) {
        return balanceRepository.findByUser(user).stream()
                .filter(balance -> balance.getBalanceType() == Balance.BalanceType.INCOME)
                .map(Balance::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getUserExpenses(User user) {
        return balanceRepository.findByUser(user).stream()
                .filter(balance -> balance.getBalanceType() == Balance.BalanceType.EXPENSE)
                .map(Balance::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
