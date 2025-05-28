package KirisShygys.service;

import KirisShygys.dto.TransactionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getUserTransactions(String token);

    TransactionDTO getTransactionById(String token, Long id);

    TransactionDTO createTransaction(TransactionDTO transactionDto, String token);

    TransactionDTO updateTransaction(String token, Long id, TransactionDTO transactionDto);

    void deleteTransaction(String token, Long id);

    void exportTransactionsToPdf(String token, HttpServletRequest request, HttpServletResponse response) throws IOException;
}

