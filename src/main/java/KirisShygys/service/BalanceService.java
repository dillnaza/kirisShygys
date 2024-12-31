package KirisShygys.service;

import KirisShygys.dto.BalanceDTO;

import java.util.List;

public interface BalanceService {
    List<BalanceDTO> getAllBalances();
    BalanceDTO getBalanceById(Long id);
    BalanceDTO createBalance(BalanceDTO balanceDto);
    BalanceDTO updateBalance(Long id, BalanceDTO balanceDto);
    void deleteBalance(Long id);
}
