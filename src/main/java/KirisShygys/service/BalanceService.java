package KirisShygys.service;

import KirisShygys.entity.User;

import java.math.BigDecimal;

public interface BalanceService {
    BigDecimal getUserTotalBalance(User user);
    BigDecimal getUserIncome(User user);
    BigDecimal getUserExpenses(User user);
}
