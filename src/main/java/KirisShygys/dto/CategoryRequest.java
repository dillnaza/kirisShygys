package KirisShygys.dto;

import KirisShygys.entity.enums.TransactionType;

public class CategoryRequest {
    private String name;
    private String icon;
    private TransactionType type;
    private Long parentCategoryId;

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}

