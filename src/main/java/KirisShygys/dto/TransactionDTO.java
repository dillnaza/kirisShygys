package KirisShygys.dto;

import KirisShygys.entity.enums.RepeatPeriod;
import KirisShygys.entity.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private CategoryDTO category;
    private AccountDTO account;
    private TagDTO tag;
    private Long userId;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datetime;
    private String place;
    private String note;
    private TransactionType type;
    private RepeatPeriod repeatPeriod;
    private LocalDateTime repeatEndDate;
    private boolean isRepeatEnabled = false;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public CategoryDTO getCategory() {
        return category;
    }
    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
    public AccountDTO getAccount() {
        return account;
    }
    public void setAccount(AccountDTO account) {
        this.account = account;
    }
    public TagDTO getTag() {
        return tag;
    }
    public void setTag(TagDTO tag) {
        this.tag = tag;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public LocalDateTime getDatetime() {
        return datetime;
    }
    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }
    public RepeatPeriod getRepeatPeriod() {
        return repeatPeriod;
    }
    public void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }
    public LocalDateTime getRepeatEndDate() {
        return repeatEndDate;
    }
    public void setRepeatEndDate(LocalDateTime repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }
    public boolean isRepeatEnabled() {
        return isRepeatEnabled;
    }
    public void setRepeatEnabled(boolean repeatEnabled) {
        isRepeatEnabled = repeatEnabled;
    }
}