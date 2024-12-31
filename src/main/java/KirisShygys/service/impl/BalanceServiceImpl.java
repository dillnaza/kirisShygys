package KirisShygys.service.impl;

import KirisShygys.dto.BalanceDTO;
import KirisShygys.entity.Balance;
import KirisShygys.repository.BalanceRepository;
import KirisShygys.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public List<BalanceDTO> getAllBalances() {
        return balanceRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public BalanceDTO getBalanceById(Long id) {
        return mapToDto(balanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Balance not found")));
    }

    @Override
    public BalanceDTO createBalance(BalanceDTO balanceDto) {
        Balance balance = mapToEntity(balanceDto);
        return mapToDto(balanceRepository.save(balance));
    }

    @Override
    public BalanceDTO updateBalance(Long id, BalanceDTO balanceDto) {
        Balance existingBalance = balanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Balance not found"));
        existingBalance.setBalance(balanceDto.getBalance());
        existingBalance.setBalanceDate(Date.valueOf(balanceDto.getBalanceDate()).toLocalDate());
        existingBalance.setBalanceType(balanceDto.getBalanceType());
        return mapToDto(balanceRepository.save(existingBalance));
    }

    @Override
    public void deleteBalance(Long id) {
        balanceRepository.deleteById(id);
    }

    private BalanceDTO mapToDto(Balance balance) {
        BalanceDTO dto = new BalanceDTO();
        dto.setId(balance.getBalanceId());
        dto.setBalance(balance.getBalance());
        dto.setBalanceDate(balance.getBalanceDate().toString());
        dto.setBalanceType(balance.getBalanceType());
        return dto;
    }

    private Balance mapToEntity(BalanceDTO dto) {
        Balance balance = new Balance();
        balance.setBalance(dto.getBalance());
        balance.setBalanceDate(Date.valueOf(dto.getBalanceDate()).toLocalDate());
        balance.setBalanceType(dto.getBalanceType());
        return balance;
    }
}
