package KirisShygys.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "balances")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Balance cannot be null")
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @NotNull(message = "Balance date cannot be null")
    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @NotNull(message = "Balance type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "balance_type", nullable = false)
    private BalanceType balanceType;

    public enum BalanceType {
        INCOME, EXPENSE, TOTAL
    }

    // Getters and Setters
    public Long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @NotNull(message = "Balance cannot be null") BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }
}
