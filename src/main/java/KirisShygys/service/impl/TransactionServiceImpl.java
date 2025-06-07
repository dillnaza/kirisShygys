package KirisShygys.service.impl;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.*;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.mapper.TransactionMapper;
import KirisShygys.repository.*;
import KirisShygys.service.StreakService;
import KirisShygys.service.TransactionService;
import KirisShygys.util.JwtUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final StreakService streakService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository,
                                  JwtUtil jwtUtil,
                                  AccountRepository accountRepository,
                                  TagRepository tagRepository,
                                  CategoryRepository categoryRepository,
                                  TransactionMapper transactionMapper,
                                  StreakService streakService) {
        super(userRepository, jwtUtil);
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
        this.streakService = streakService;
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
        transaction.setPinned(dto.isPinned());
        transaction = transactionRepository.save(transaction);
        logger.info("Transaction created with ID: {} for user: {}", transaction.getId(), user.getEmail());
        if (dto.getDatetime().toLocalDate().isEqual(java.time.LocalDate.now())) {
            streakService.lightFireAutomatically(user.getId());
        }
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
        transaction.setPinned(dto.isPinned());
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

    @Override
    public void exportTransactionsToPdf(String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(token);
        String lang = request.getHeader("Accept-Language");
        if (lang == null || (!lang.equalsIgnoreCase("kz") && !lang.equalsIgnoreCase("en"))) {
            lang = "ru";
        } else {
            lang = lang.toLowerCase();
        }
        String fromParam = request.getParameter("from");
        String toParam = request.getParameter("to");
        List<Transaction> transactions = transactionRepository.findByUser(user).stream()
                .filter(t -> {
                    if (fromParam != null && !fromParam.isBlank()) {
                        if (t.getDatetime().isBefore(java.time.LocalDateTime.parse(fromParam))) return false;
                    }
                    if (toParam != null && !toParam.isBlank()) {
                        return !t.getDatetime().isAfter(java.time.LocalDateTime.parse(toParam));
                    }
                    return true;
                })
                .toList();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.pdf");
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/SF-Pro/SF-Pro.ttf");
        byte[] fontBytes = fontStream.readAllBytes();
        PdfFont font = PdfFontFactory.createFont(
                fontBytes,
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
        );
        document.setFont(font);
        Locale locale = switch (lang) {
            case "kz" -> new Locale("kk");
            case "en" -> Locale.ENGLISH;
            default -> new Locale("ru");
        };
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", locale);
        DateTimeFormatter periodFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", locale);
        String title = switch (lang) {
            case "kz" -> "Пайдаланушы транзакциялары: ";
            case "en" -> "User transactions: ";
            default -> "Транзакции пользователя: ";
        };
        String totalLabel = switch (lang) {
            case "kz" -> "Жазбалар саны: ";
            case "en" -> "Total records: ";
            default -> "Всего записей: ";
        };
        String incomeLabel = switch (lang) {
            case "kz" -> "Кіріс";
            case "en" -> "Income";
            default -> "Доход";
        };
        String expenseLabel = switch (lang) {
            case "kz" -> "Шығыс";
            case "en" -> "Expense";
            default -> "Расход";
        };
        String[] headers = switch (lang) {
            case "kz" -> new String[]{"Күні", "Түрі", "Сомасы", "Санат", "Сипаттама", "Шот", "Тег", "Орны"};
            case "en" -> new String[]{"Date", "Type", "Amount", "Category", "Description", "Account", "Tag", "Place"};
            default -> new String[]{"Дата", "Тип", "Сумма", "Категория", "Описание", "Счёт", "Тег", "Место"};
        };
        document.add(new Paragraph(title + user.getEmail()).setBold().setFontSize(16).setMarginBottom(5));
        String dateRangeText = "";
        if (lang.equals("kz")) {
            if (fromParam != null && !fromParam.isBlank()) {
                dateRangeText += java.time.LocalDateTime.parse(fromParam).format(periodFormatter) + " бастап";
            }
            if (toParam != null && !toParam.isBlank()) {
                if (!dateRangeText.isEmpty()) dateRangeText += " ";
                dateRangeText += java.time.LocalDateTime.parse(toParam).format(periodFormatter) + " дейін";
            }
        } else {
            if (fromParam != null && !fromParam.isBlank()) {
                dateRangeText += switch (lang) {
                    case "en" -> "from " + java.time.LocalDateTime.parse(fromParam).format(periodFormatter);
                    default -> "с " + java.time.LocalDateTime.parse(fromParam).format(periodFormatter);
                };
            }
            if (toParam != null && !toParam.isBlank()) {
                if (!dateRangeText.isEmpty()) dateRangeText += " ";
                dateRangeText += switch (lang) {
                    case "en" -> "to " + java.time.LocalDateTime.parse(toParam).format(periodFormatter);
                    default -> "по " + java.time.LocalDateTime.parse(toParam).format(periodFormatter);
                };
            }
        }
        if (!dateRangeText.isEmpty()) {
            document.add(new Paragraph(dateRangeText).setItalic().setMarginBottom(10));
        }
        document.add(new Paragraph(totalLabel + transactions.size()).setMarginBottom(10));
        Table table = new Table(headers.length).setWidth(UnitValue.createPercentValue(100));
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setBold()));
        }
        for (Transaction t : transactions) {
            String formattedDate = t.getDatetime().format(dateTimeFormatter);
            String category = "-";
            if (t.getCategory() != null) {
                category = switch (lang) {
                    case "kz" -> t.getCategory().getNameKz();
                    case "en" -> t.getCategory().getNameEn();
                    default -> t.getCategory().getNameRu();
                };
            }
            table.addCell(formattedDate);
            table.addCell(expenseLabel);
            table.addCell(t.getAmount() + " ₸");
            table.addCell(category);
            table.addCell(t.getNote() != null ? t.getNote() : "-");
            table.addCell(t.getAccount() != null ? t.getAccount().getName() : "-");
            table.addCell(t.getTag() != null ? t.getTag().getName() : "-");
            table.addCell(t.getPlace() != null ? t.getPlace() : "-");
        }
        document.add(table);
        document.close();
    }
}