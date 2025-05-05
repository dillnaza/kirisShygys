package KirisShygys.dto;

public class CategoryRequest {
    private String name;
    private String icon;
    private Long parentCategoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }
}

