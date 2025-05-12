package KirisShygys.dto;

import KirisShygys.entity.enums.TransactionType;

public class CategoryDTO {
    private Long id;
    private String name;
    private String icon;
    private TransactionType type;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public TransactionType getType() { return type; }
}
