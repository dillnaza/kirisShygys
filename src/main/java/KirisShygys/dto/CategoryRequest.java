package KirisShygys.dto;

public class CategoryRequest {
    private String name;
    private Long parentCategoryId;

    public String getName() {
        return name;
    }


    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}

