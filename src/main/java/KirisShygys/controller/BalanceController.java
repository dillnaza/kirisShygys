package KirisShygys.controller;

import KirisShygys.dto.BalanceDTO;
import KirisShygys.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/balances")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping
    public List<BalanceDTO> getAllBalances() {
        return balanceService.getAllBalances();
    }

    @GetMapping("/{id}")
    public BalanceDTO getBalanceById(@PathVariable Long id) {
        return balanceService.getBalanceById(id);
    }

    @PostMapping
    public BalanceDTO createBalance(@RequestBody BalanceDTO balanceDto) {
        return balanceService.createBalance(balanceDto);
    }

    @PutMapping("/{id}")
    public BalanceDTO updateBalance(@PathVariable Long id, @RequestBody BalanceDTO balanceDto) {
        return balanceService.updateBalance(id, balanceDto);
    }

    @DeleteMapping("/{id}")
    public void deleteBalance(@PathVariable Long id) {
        balanceService.deleteBalance(id);
    }
}