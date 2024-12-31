package KirisShygys.service.impl;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.Transaction;
import KirisShygys.repository.TransactionRepository;
import KirisShygys.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        return mapToDto(transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found")));
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