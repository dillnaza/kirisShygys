package KirisShygys.service;

import KirisShygys.dto.AccountDTO;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAllAccounts();
    AccountDTO getAccountById(Long id);
    AccountDTO createAccount(AccountDTO accountDto);
    AccountDTO updateAccount(Long id, AccountDTO accountDto);
    void deleteAccount(Long id);
}
