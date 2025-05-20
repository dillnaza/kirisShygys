package KirisShygys.dto;

import KirisShygys.entity.enums.TransactionType;

public class CategoryRequest {
    private String nameRu;
    private String nameKz;
    private String nameEn;
    private String icon;
    private TransactionType type;
    private Long parentCategoryId;

    public String getNameRu() {
        return nameRu;
    }
    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }
    public String getNameKz() {
        return nameKz;
    }
    public void setNameKz(String nameKz) {
        this.nameKz = nameKz;
    }
    public String getNameEn() {
        return nameEn;
    }
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}

