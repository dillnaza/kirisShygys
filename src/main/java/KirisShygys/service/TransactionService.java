package KirisShygys.service;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;

import java.util.List;

public interface TransactionService {
    List<Transaction> getUserTransactions(User user);
    TransactionDTO createTransaction(TransactionDTO transactionDto);
    TransactionDTO updateTransaction(Long id, TransactionDTO transactionDto);
    void deleteTransaction(Long id);
}
