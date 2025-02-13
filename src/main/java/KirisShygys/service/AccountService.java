package KirisShygys.service;

import KirisShygys.dto.AccountDTO;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAllAccounts();
    AccountDTO createAccount(AccountDTO accountDto);
    AccountDTO updateAccount(Long id, AccountDTO accountDto);
    void deleteAccount(Long id);
}
