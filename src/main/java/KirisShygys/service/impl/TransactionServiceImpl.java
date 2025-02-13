package KirisShygys.service.impl;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;
import KirisShygys.repository.TransactionRepository;
import KirisShygys.service.TransactionService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getUserTransactions(User user, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return transactionRepository.findByUserAndDatetimeBetween(user, dateFrom, dateTo);
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDto) {
        Transaction transaction = mapToEntity(transactionDto);
        return mapToDto(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDto) {
        Transaction existingTransaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        existingTransaction.setType(transactionDto.getType());
        existingTransaction.setDatetime(Timestamp.valueOf(transactionDto.getDatetime()).toLocalDateTime());
        return mapToDto(transactionRepository.save(existingTransaction));
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    private TransactionDTO mapToDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getTransactionId());
        dto.setType(transaction.getType());
        dto.setDatetime(transaction.getDatetime().toString());
        return dto;
    }

    private Transaction mapToEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setType(dto.getType());
        transaction.setDatetime(Timestamp.valueOf(dto.getDatetime()).toLocalDateTime());
        return transaction;
    }
}
