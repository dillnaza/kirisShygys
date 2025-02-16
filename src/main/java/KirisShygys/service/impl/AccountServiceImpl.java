package KirisShygys.service.impl;

import KirisShygys.dto.AccountDTO;
import KirisShygys.entity.Account;
import KirisShygys.repository.AccountRepository;
import KirisShygys.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        return mapToDto(accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found")));
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDto) {
        Account account = mapToEntity(accountDto);
        return mapToDto(accountRepository.save(account));
    }

    @Override
    public AccountDTO updateAccount(Long id, AccountDTO accountDto) {
        Account existingAccount = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        existingAccount.setName(accountDto.getName());
        return mapToDto(accountRepository.save(existingAccount));
    }

    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    private AccountDTO mapToDto(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getAccountId());
        dto.setName(account.getName());
        return dto;
    }

    private Account mapToEntity(AccountDTO dto) {
        Account account = new Account();
        account.setName(dto.getName());
        return account;
    }
}