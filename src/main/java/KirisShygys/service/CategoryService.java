package KirisShygys.service;

import KirisShygys.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getCategories(String token);
    Category getCategoryById(String token, Long id);
    Category createCategory(String token, Category category);
    Category updateCategory(String token, Long id, Category category);
    void deleteCategory(String token, Long id);
}
