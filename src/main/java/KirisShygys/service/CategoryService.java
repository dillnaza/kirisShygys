package KirisShygys.service;

import KirisShygys.dto.CategoryRequest;
import KirisShygys.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getCategories(String token);
    Category getCategoryById(String token, Long id);
    Category createCategory(String token, CategoryRequest request);
    Category updateCategory(String token, Long id, CategoryRequest request);
    void deleteCategory(String token, Long id);
}
