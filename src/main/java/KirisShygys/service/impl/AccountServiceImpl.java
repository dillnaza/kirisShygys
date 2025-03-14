package KirisShygys.service.impl;

import KirisShygys.entity.Account;
import KirisShygys.entity.User;
import KirisShygys.repository.AccountRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.AccountService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl extends TransactionEntityService<Account, Long> implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        super(accountRepository, "Account", userRepository, jwtUtil);
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account create(String token, Account account) {
        User user = getAuthenticatedUser(token);
        account.setUser(user);
        return super.create(token, account);
    }
}
