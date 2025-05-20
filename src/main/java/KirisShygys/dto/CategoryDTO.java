package KirisShygys.dto;

import KirisShygys.entity.enums.TransactionType;

public class CategoryDTO {
    private Long id;
    private String nameRu;
    private String nameKz;
    private String nameEn;
    private String icon;
    private TransactionType type;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String nameRu, String nameKz, String nameEn, String icon, TransactionType type) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameKz = nameKz;
        this.nameEn = nameEn;
        this.icon = icon;
        this.type = type;
    }

    public Long getId() { return id; }
    public String getNameRu() {
        return nameRu;
    }
    public String getNameKz() {
        return nameKz;
    }
    public String getNameEn() {
        return nameEn;
    }
    public String getIcon() { return icon; }
    public TransactionType getType() { return type; }
}
