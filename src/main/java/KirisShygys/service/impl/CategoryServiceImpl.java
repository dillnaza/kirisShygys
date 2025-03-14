package KirisShygys.service.impl;

import KirisShygys.dto.CategoryRequest;
import KirisShygys.entity.Category;
import KirisShygys.entity.User;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.repository.CategoryRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.CategoryService;
import KirisShygys.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends TransactionEntityService<Category, Long> implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        super(categoryRepository, "Category", userRepository, jwtUtil);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories(String token) {
        User user = getAuthenticatedUser(token);
        List<Category> categories = categoryRepository.findByUser(user);
        return categories.stream()
                .filter(category -> category.getParentCategory() == null)
                .toList();
    }

    @Override
    public Category getCategoryById(String token, Long id) {
        User user = getAuthenticatedUser(token);
        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));
    }

    @Transactional
    public Category createCategory(String token, CategoryRequest request) {
        User user = getAuthenticatedUser(token);
        Category category = new Category();
        category.setName(request.getName());
        category.setUser(user);
        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));
            if (!parentCategory.getUser().equals(user)) {
                throw new UnauthorizedException("Parent category does not belong to the user");
            }
            if (parentCategory.getParentCategory() != null) {
                throw new IllegalArgumentException("Nested subcategories (grandchildren) are not allowed.");
            }
            category.setParentCategory(parentCategory);
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(String token, Long id, Category updatedCategory) {
        User user = getAuthenticatedUser(token);
        Category existingCategory = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));
        existingCategory.setName(updatedCategory.getName());
        if (updatedCategory.getParentCategory() != null) {
            Category parentCategory = categoryRepository.findById(updatedCategory.getParentCategory().getId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));
            if (!parentCategory.getUser().equals(user)) {
                throw new UnauthorizedException("Parent category does not belong to the user");
            }
            if (parentCategory.getParentCategory() != null) {
                throw new IllegalArgumentException("Nested subcategories (grandchildren) are not allowed.");
            }
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }
        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(String token, Long categoryId) {
        User user = getAuthenticatedUser(token);
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.deleteByParentCategory(category);
        categoryRepository.delete(category);
    }
}
