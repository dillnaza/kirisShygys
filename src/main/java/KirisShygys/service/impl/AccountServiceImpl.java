package KirisShygys.service.impl;

import KirisShygys.entity.Account;
import KirisShygys.entity.User;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
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

    @Override
    @Transactional
    public Account update(String token, Long id, Account updatedAccount) {
        User user = getAuthenticatedUser(token);
        Account existingAccount = super.getById(token, id);
        if (!existingAccount.getUser().equals(user)) {
            throw new UnauthorizedException("You do not have permission to update this account.");
        }
        existingAccount.setName(updatedAccount.getName());
        return accountRepository.save(existingAccount);
    }
}
