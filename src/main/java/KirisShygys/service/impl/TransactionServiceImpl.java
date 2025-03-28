package KirisShygys.service.impl;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.*;
import KirisShygys.mapper.TransactionMapper;
import KirisShygys.repository.*;
import KirisShygys.service.TransactionService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository,
                                  JwtUtil jwtUtil,
                                  AccountRepository accountRepository,
                                  TagRepository tagRepository,
                                  CategoryRepository categoryRepository,
                                  TransactionMapper transactionMapper) {
        super(userRepository, jwtUtil);
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<TransactionDTO> getUserTransactions(String token) {
        User user = getAuthenticatedUser(token);
        List<Transaction> transactions = transactionRepository.findByUser(user);
        System.out.println("Transactions found: " + transactions.size());
        return transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDto, String token) {
        User user = getAuthenticatedUser(token);
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Tag tag = (transactionDto.getTagId() != null) ? tagRepository.findById(transactionDto.getTagId()).orElse(null) : null;
        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setTag(tag);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(String token, Long id, TransactionDTO transactionDto) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDatetime(transactionDto.getDatetime());
        transaction.setPlace(transactionDto.getPlace());
        transaction.setNote(transactionDto.getNote());
        transaction.setType(transactionDto.getType());
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        transactionRepository.deleteById(id);
    }
}
