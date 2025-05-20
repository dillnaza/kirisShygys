package KirisShygys.mapper;

import KirisShygys.dto.AccountDTO;
import KirisShygys.dto.CategoryDTO;
import KirisShygys.dto.TagDTO;
import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.*;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionDTO toDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setCategory(transaction.getCategory() != null
                ? new CategoryDTO(
                transaction.getCategory().getId(),
                transaction.getCategory().getNameRu(),
                transaction.getCategory().getNameKz(),
                transaction.getCategory().getNameEn(),
                transaction.getCategory().getIcon(),
                transaction.getCategory().getType())
                : null);
        dto.setAccount(transaction.getAccount() != null
                ? new AccountDTO(transaction.getAccount().getId(), transaction.getAccount().getName())
                : null);
        dto.setTag(transaction.getTag() != null
                ? new TagDTO(transaction.getTag().getId(), transaction.getTag().getName())
                : null);
        dto.setUserId(transaction.getUser().getId());
        dto.setAmount(transaction.getAmount());
        dto.setDatetime(transaction.getDatetime());
        dto.setPlace(transaction.getPlace());
        dto.setNote(transaction.getNote());
        dto.setType(transaction.getType());
        dto.setRepeatEnabled(transaction.isRepeatEnabled());
        dto.setRepeatPeriod(transaction.getRepeatPeriod());
        dto.setRepeatEndDate(transaction.getRepeatEndDate());
        dto.setPinned(transaction.isPinned());
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
        transaction.setPinned(dto.isPinned());
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            transaction.setCategory(new Category(dto.getCategory().getId()));
        } else {
            throw new IllegalArgumentException("Category is required");
        }
        if (dto.getAccount() != null && dto.getAccount().getId() != null) {
            transaction.setAccount(new Account(dto.getAccount().getId()));
        } else {
            transaction.setAccount(null);
        }
        if (dto.getTag() != null && dto.getTag().getId() != null) {
            transaction.setTag(new Tag(dto.getTag().getId()));
        } else {
            transaction.setTag(null);
        }
        return transaction;
    }
}
