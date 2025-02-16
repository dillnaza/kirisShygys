package KirisShygys.dto;

import KirisShygys.entity.Balance;

import java.math.BigDecimal;

public class BalanceDTO {
    private Long id;
    private BigDecimal balance;
    private Balance.BalanceType balanceType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Balance.BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(Balance.BalanceType balanceType) {
        this.balanceType = balanceType;
    }
}
