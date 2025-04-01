package KirisShygys.service;

import KirisShygys.dto.TransactionDTO;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getUserTransactions(String token);
    TransactionDTO getTransactionById(String token, Long id);
    TransactionDTO createTransaction(TransactionDTO transactionDto, String token);
    TransactionDTO updateTransaction(String token, Long id, TransactionDTO transactionDto);
    void deleteTransaction(String token, Long id);
}
