package KirisShygys.service;

import KirisShygys.dto.BalanceDTO;

import java.time.LocalDate;

public interface BalanceService {
    BalanceDTO getUserBalance(String token);
    BalanceDTO getUserBalanceByPeriod(String token, LocalDate startDate, LocalDate endDate);
}
