package KirisShygys.service;

import KirisShygys.entity.Account;
import KirisShygys.entity.User;

import java.util.List;

public interface AccountService {
    List<Account> getAccounts(String token);
    Account createAccount(String token, Account account);
    Account updateAccount(String token, Long id, Account updatedAccount);
    void deleteAccount(String token, Long id);
}