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
        Transaction transaction = findTransactionByIdAndCheckOwnership(id, user);
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO dto, String token) {
        User user = getAuthenticatedUser(token);
        Account account = resolveAccount(dto);
        Category category = resolveCategory(dto);
        Tag tag = resolveTag(dto);
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setTag(tag);
        transaction.setRepeatEnabled(dto.isRepeatEnabled());
        transaction.setRepeatPeriod(dto.getRepeatPeriod());
        transaction.setRepeatEndDate(dto.getRepeatEndDate());
        transaction = transactionRepository.save(transaction);
        logger.info("Transaction created with ID: {} for user: {}", transaction.getId(), user.getEmail());
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(String token, Long id, TransactionDTO dto) {
        User user = getAuthenticatedUser(token);
        Transaction transaction = findTransactionByIdAndCheckOwnership(id, user);
        transaction.setAccount(resolveAccount(dto));
        transaction.setCategory(resolveCategory(dto));
        transaction.setTag(resolveTag(dto));
        transaction.setAmount(dto.getAmount());
        transaction.setDatetime(dto.getDatetime());
        transaction.setPlace(dto.getPlace());
        transaction.setNote(dto.getNote());
        transaction.setType(dto.getType());
        transaction.setRepeatEnabled(dto.isRepeatEnabled());
        transaction.setRepeatPeriod(dto.getRepeatPeriod());
        transaction.setRepeatEndDate(dto.getRepeatEndDate());
        transaction = transactionRepository.save(transaction);
        logger.info("Transaction updated with ID: {} by user: {}", id, user.getEmail());
        return transactionMapper.toDto(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(String token, Long id) {
        User user = getAuthenticatedUser(token);
        transactionRepository.deleteById(id);
        logger.info("Transaction deleted with ID: {} by user: {}", id, user.getEmail());
    }

    private Transaction findTransactionByIdAndCheckOwnership(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction with ID " + id + " not found"));
        if (!transaction.getUser().equals(user)) {
            throw new UnauthorizedException("You do not have permission to access this transaction.");
        }
        return transaction;
    }

    private Account resolveAccount(TransactionDTO dto) {
        if (dto.getAccount() == null || dto.getAccount().getId() == null) return null;
        Account account = accountRepository.findById(dto.getAccount().getId())
                .orElseThrow(() -> new NotFoundException("Account with ID " + dto.getAccount().getId() + " not found"));
        if (account.isDeleted()) throw new NotFoundException("Account is deleted");
        return account;
    }

    private Category resolveCategory(TransactionDTO dto) {
        Category category = categoryRepository.findById(dto.getCategory().getId())
                .orElseThrow(() -> new NotFoundException("Category with ID " + dto.getCategory().getId() + " not found"));
        if (category.isDeleted()) throw new NotFoundException("Category is deleted");
        return category;
    }

    private Tag resolveTag(TransactionDTO dto) {
        if (dto.getTag() == null || dto.getTag().getId() == null) return null;
        return tagRepository.findById(dto.getTag().getId())
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new NotFoundException("Tag with ID " + dto.getTag().getId() + " not found or is deleted"));
    }
}