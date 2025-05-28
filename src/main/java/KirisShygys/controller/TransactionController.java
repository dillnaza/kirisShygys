package KirisShygys.controller;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;


import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private String getAuthToken(HttpServletRequest request) {
        return (String) request.getAttribute("AuthToken");
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getUserTransactions(getAuthToken(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(HttpServletRequest request,
                                                             @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(getAuthToken(request), id));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(HttpServletRequest request,
                                                            @RequestBody TransactionDTO transactionDto) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDto, getAuthToken(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(HttpServletRequest request,
                                                            @PathVariable Long id,
                                                            @RequestBody TransactionDTO transactionDto) {
        return ResponseEntity.ok(transactionService.updateTransaction(getAuthToken(request), id, transactionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(HttpServletRequest request,
                                                  @PathVariable Long id) {
        transactionService.deleteTransaction(getAuthToken(request), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/pdf")
    public void exportTransactionsPdf(HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        String token = getAuthToken(request);
        transactionService.exportTransactionsToPdf(token, request, response);
    }
}
