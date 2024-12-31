package KirisShygys.controller;

import KirisShygys.dto.AccountDTO;
import KirisShygys.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public AccountDTO createAccount(@RequestBody AccountDTO accountDto) {
        return accountService.createAccount(accountDto);
    }

    @PutMapping("/{id}")
    public AccountDTO updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDto) {
        return accountService.updateAccount(id, accountDto);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}