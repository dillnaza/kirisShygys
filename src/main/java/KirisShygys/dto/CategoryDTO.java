package KirisShygys.dto;

public class CategoryDTO {
    private Long id;
    private String name;
    private String icon;

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
}
