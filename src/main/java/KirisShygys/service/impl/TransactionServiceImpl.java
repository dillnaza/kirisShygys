package KirisShygys.service.impl;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.*;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.mapper.TransactionMapper;
import KirisShygys.repository.*;
import KirisShygys.service.TransactionService;
import KirisShygys.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
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
        logger.info("Found {} transactions for user: {}", transactions.size(), user.getEmail());
        return transactions.stream().map(transactionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Transaction with ID {} not found", id);
                    return new NotFoundException("Transaction with ID " + id + " not found");
                });
        if (!transaction.getUser().equals(user)) {
            logger.warn("User {} attempted unauthorized access to transaction ID {}", user.getEmail(), id);
            throw new UnauthorizedException("You do not have permission to access this transaction.");
        }
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDto, String token) {
        User user = getAuthenticatedUser(token);
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account with ID " + transactionDto.getAccountId() + " not found"));
        Category category = categoryRepository.findById(transactionDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category with ID " + transactionDto.getCategoryId() + " not found"));
        Tag tag = (transactionDto.getTagId() != null) ? tagRepository.findById(transactionDto.getTagId()).orElse(null) : null;
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setTag(tag);
        transaction = transactionRepository.save(transaction);
        logger.info("Transaction created with ID: {} for user: {}", transaction.getId(), user.getEmail());
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(String token, Long id, TransactionDTO transactionDto) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Transaction with ID {} not found", id);
                    return new NotFoundException("Transaction with ID " + id + " not found");
                });
        if (!transaction.getUser().equals(user)) {
            logger.warn("User {} attempted unauthorized update on transaction ID {}", user.getEmail(), id);
            throw new UnauthorizedException("You do not have permission to update this transaction.");
        }
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDatetime(transactionDto.getDatetime());
        transaction.setPlace(transactionDto.getPlace());
        transaction.setNote(transactionDto.getNote());
        transaction.setType(transactionDto.getType());
        transaction = transactionRepository.save(transaction);
        logger.info("Transaction updated with ID: {} by user: {}", id, user.getEmail());
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Transaction with ID {} not found", id);
                    return new NotFoundException("Transaction with ID " + id + " not found");
                });
        if (!transaction.getUser().equals(user)) {
            logger.warn("User {} attempted unauthorized deletion of transaction ID {}", user.getEmail(), id);
            throw new UnauthorizedException("You do not have permission to delete this transaction.");
        }
        transactionRepository.deleteById(id);
        logger.info("Transaction deleted with ID: {} by user: {}", id, user.getEmail());
    }
}