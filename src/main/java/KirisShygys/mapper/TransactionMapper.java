package KirisShygys.mapper;

import KirisShygys.dto.TransactionDTO;
import KirisShygys.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionDTO toDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setCategoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null);
        dto.setAccountId(transaction.getAccount() != null ? transaction.getAccount().getId() : null);
        dto.setTagId(transaction.getTag() != null ? transaction.getTag().getId() : null);
        dto.setAmount(transaction.getAmount());
        dto.setDatetime(transaction.getDatetime());
        dto.setPlace(transaction.getPlace());
        dto.setNote(transaction.getNote());
        dto.setType(transaction.getType());
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
        return transaction;
    }
}
