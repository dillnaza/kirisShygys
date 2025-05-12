package KirisShygys.service.impl;

import KirisShygys.dto.CategoryRequest;
import KirisShygys.entity.Category;
import KirisShygys.entity.User;
import KirisShygys.entity.enums.TransactionType;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.repository.CategoryRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.CategoryService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends TransactionEntityService<Category, Long> implements CategoryService {

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
        category.setIcon(request.getIcon());
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
    public Category updateCategory(String token, Long id, CategoryRequest request) {
        User user = getAuthenticatedUser(token);
        Category existingCategory = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));
        existingCategory.setName(request.getName());
        existingCategory.setIcon(request.getIcon());
        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
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

    @Override
    @Transactional
    public void createDefaultCategories(User user) {
        List<Category> defaultCategories = List.of(
                create("Зарплата", "💼", TransactionType.INCOME, user),
                create("Подарок", "🎁", TransactionType.INCOME, user),
                create("Еда", "🍔", TransactionType.EXPENSE, user),
                create("Транспорт", "🚕", TransactionType.EXPENSE, user),
                create("Развлечения", "🎮", TransactionType.EXPENSE, user)
        );
        categoryRepository.saveAll(defaultCategories);
    }

    private Category create(String name, String icon, TransactionType type, User user) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setType(type);
        category.setUser(user);
        return category;
    }
}
