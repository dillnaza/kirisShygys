package KirisShygys.dto;

import java.math.BigDecimal;
import java.util.List;

public class BalanceDTO {
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal balance;
    private List<TransactionDTO> transactions;

    public BalanceDTO() {
    }

    public BalanceDTO(BigDecimal income, BigDecimal expenses, BigDecimal balance, List<TransactionDTO> transactions) {
        this.income = income;
        this.expenses = expenses;
        this.balance = balance;
        this.transactions = transactions;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }
}
