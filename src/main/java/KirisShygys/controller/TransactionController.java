package KirisShygys.controller;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public TransactionDTO getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody TransactionDTO transactionDto) {
        return transactionService.createTransaction(transactionDto);
    }

    @PutMapping("/{id}")
    public TransactionDTO updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDto) {
        return transactionService.updateTransaction(id, transactionDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}

