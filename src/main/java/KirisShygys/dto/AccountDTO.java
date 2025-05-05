package KirisShygys.dto;

public class AccountDTO {
    private Long id;
    private String name;

    public AccountDTO() {
    }

    public AccountDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
