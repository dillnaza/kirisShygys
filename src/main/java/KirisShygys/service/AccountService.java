package KirisShygys.service;

import KirisShygys.entity.Account;
import KirisShygys.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserUserId(userId);
    }
}
