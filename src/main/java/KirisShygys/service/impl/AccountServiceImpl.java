package KirisShygys.service.impl;

import KirisShygys.entity.Account;
import KirisShygys.repository.AccountRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.AccountService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends TransactionEntityService<Account, Long> implements AccountService {

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        super(accountRepository, "Account", userRepository, jwtUtil);
    }
}
