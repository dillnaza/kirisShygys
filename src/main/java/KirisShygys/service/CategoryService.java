package KirisShygys.service;

import KirisShygys.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDto);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDto);
    void deleteCategory(Long id);
}
