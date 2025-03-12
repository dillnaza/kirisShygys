package KirisShygys.service.impl;

import KirisShygys.entity.Account;
import KirisShygys.entity.User;
import KirisShygys.exception.ForbiddenException;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.repository.AccountRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.AccountService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private User getAuthenticatedUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Missing authentication token");
        }

        String email = jwtUtil.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token"));
    }

    @Override
    public List<Account> getAccounts(String token) {
        User user = getAuthenticatedUser(token);
        return accountRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Account createAccount(String token, Account account) {
        User user = getAuthenticatedUser(token);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccount(String token, Long id, Account updatedAccount) {
        User user = getAuthenticatedUser(token);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account with ID " + id + " not found"));

        if (!account.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You do not have permission to modify this account");
        }

        account.setName(updatedAccount.getName());
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteAccount(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account with ID " + id + " not found"));

        if (!account.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You do not have permission to delete this account");
        }

        accountRepository.delete(account);
    }
}
