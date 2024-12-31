package KirisShygys.service;

import KirisShygys.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();
    TransactionDTO getTransactionById(Long id);
    TransactionDTO createTransaction(TransactionDTO transactionDto);
    TransactionDTO updateTransaction(Long id, TransactionDTO transactionDto);
    void deleteTransaction(Long id);
}
