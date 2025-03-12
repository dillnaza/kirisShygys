package KirisShygys.controller;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;
import KirisShygys.service.TransactionService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
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