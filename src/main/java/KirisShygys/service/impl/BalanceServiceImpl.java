package KirisShygys.service.impl;

import KirisShygys.dto.*;
import KirisShygys.entity.Transaction;
import KirisShygys.entity.User;
import KirisShygys.entity.enums.TransactionType;
import KirisShygys.mapper.TransactionMapper;
import KirisShygys.repository.TransactionRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.BalanceService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TransactionMapper transactionMapper;

    public BalanceServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository, JwtUtil jwtUtil, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public BalanceDTO getUserBalance(String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUser(user);
        return calculateBalance(transactions);
    }

    @Override
    public BalanceDTO getUserBalanceByPeriod(String token, LocalDate startDate, LocalDate endDate) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUserAndDatetimeBetween(
                user, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
        );
        return calculateBalance(transactions);
    }

    private BalanceDTO calculateBalance(List<Transaction> transactions) {
        BigDecimal income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = income.subtract(expenses);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
        return new BalanceDTO(income, expenses, balance, transactionDTOs);
    }
}
