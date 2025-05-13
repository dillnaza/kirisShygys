package KirisShygys.mapper;

import KirisShygys.dto.AccountDTO;
import KirisShygys.dto.CategoryDTO;
import KirisShygys.dto.TagDTO;
import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionDTO toDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setCategory(transaction.getCategory() != null
                ? new CategoryDTO(
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getCategory().getIcon(),
                transaction.getCategory().getType())
                : null);
        dto.setAccount(transaction.getAccount() != null
                ? new AccountDTO(transaction.getAccount().getId(), transaction.getAccount().getName())
                : null);
        dto.setTag(transaction.getTag() != null
                ? new TagDTO(transaction.getTag().getId(), transaction.getTag().getName())
                : null);
        dto.setAmount(transaction.getAmount());
        dto.setDatetime(transaction.getDatetime());
        dto.setPlace(transaction.getPlace());
        dto.setNote(transaction.getNote());
        dto.setType(transaction.getType());
        dto.setRepeatEnabled(transaction.isRepeatEnabled());
        dto.setRepeatPeriod(transaction.getRepeatPeriod());
        dto.setRepeatEndDate(transaction.getRepeatEndDate());

        return dto;
    }

    public Transaction toEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setAmount(dto.getAmount());
        transaction.setDatetime(dto.getDatetime());
        transaction.setPlace(dto.getPlace());
        transaction.setNote(dto.getNote());
        transaction.setType(dto.getType());
        transaction.setRepeatEnabled(dto.isRepeatEnabled());
        transaction.setRepeatPeriod(dto.getRepeatPeriod());
        transaction.setRepeatEndDate(dto.getRepeatEndDate());
        return transaction;
    }
}
