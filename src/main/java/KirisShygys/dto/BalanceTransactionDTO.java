package KirisShygys.dto;

import KirisShygys.entity.enums.TransactionType;

import java.math.BigDecimal;

public class BalanceTransactionDTO {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String accountName;
    private String categoryName;
    private String tagName;

    public BalanceTransactionDTO(Long id, BigDecimal amount,TransactionType type,
                                 String accountName, String categoryName, String tagName) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.accountName = accountName;
        this.categoryName = categoryName;
        this.tagName = tagName;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getAccountName() { return accountName; }
    public String getCategoryName() { return categoryName; }
    public String getTagName() { return tagName; }
}
