package KirisShygys.controller;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(transactionService.getUserTransactions(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(transactionService.getTransactionById(token, id));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestHeader("Authorization") String token,
            @RequestBody TransactionDTO transactionDto) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(transactionService.createTransaction(transactionDto, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody TransactionDTO transactionDto) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(transactionService.updateTransaction(token, id, transactionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        transactionService.deleteTransaction(token, id);
        return ResponseEntity.noContent().build();
    }
}
