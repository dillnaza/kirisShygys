package KirisShygys.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "Balances")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "balance_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date balanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "balance_type", nullable = false)
    private BalanceType balanceType;

    public enum BalanceType {
        INCOME, EXPENSE, TOTAL
    }
}