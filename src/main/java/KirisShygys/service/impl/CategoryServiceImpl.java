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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        category.setType(request.getType());
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
        existingCategory.setType(request.getType());
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
        Map<String, Category> parentMap = new HashMap<>();
        List<Category> parents = List.of(
                create("Государственные выплаты", "🏛️", TransactionType.INCOME, user, null),
                create("Возвраты", "🔄", TransactionType.INCOME, user, null),
                create("Транспорт", "🚗", TransactionType.EXPENSE, user, null),
                create("Финансовые обязательства", "💳", TransactionType.EXPENSE, user, null)
        );
        for (Category parent : parents) {
            categoryRepository.save(parent);
            parentMap.put(parent.getName(), parent);
        }
        List<Category> categories = List.of(
                create("Зарплата", "💼", TransactionType.INCOME, user, null),
                create("Стипендия", "🎓", TransactionType.INCOME, user, parentMap.get("Государственные выплаты")),
                create("Пособие", "📩", TransactionType.INCOME, user, parentMap.get("Государственные выплаты")),
                create("Подарки", "🎁", TransactionType.INCOME, user, null),
                create("Пассивный доход", "🏠", TransactionType.INCOME, user, null),
                create("Налоги", "💸", TransactionType.INCOME, user, parentMap.get("Возвраты")),
                create("Долги", "🔁", TransactionType.INCOME, user, parentMap.get("Возвраты")),
                create("Кэшбэк / Бонусы", "🎉", TransactionType.INCOME, user, null),
                create("Продукты питания", "🍎", TransactionType.EXPENSE, user, null),
                create("Аренда", "🏠", TransactionType.EXPENSE, user, null),
                create("Коммунальные услуги", "💡", TransactionType.EXPENSE, user, null),
                create("Мобильная связь / Интернет", "📶", TransactionType.EXPENSE, user, null),
                create("Здоровье", "💊", TransactionType.EXPENSE, user, null),
                create("Красота и уход", "💅", TransactionType.EXPENSE, user, null),
                create("Образование / Курсы", "📚", TransactionType.EXPENSE, user, null),
                create("Подписки", "📺", TransactionType.EXPENSE, user, null),
                create("Подарки другим", "🎁", TransactionType.EXPENSE, user, null),
                create("Такси", "🚕", TransactionType.EXPENSE, user, parentMap.get("Транспорт")),
                create("Бензин", "⛽", TransactionType.EXPENSE, user, parentMap.get("Транспорт")),
                create("Кредиты", "💳", TransactionType.EXPENSE, user, parentMap.get("Финансовые обязательства")),
                create("Долги", "📉", TransactionType.EXPENSE, user, parentMap.get("Финансовые обязательства")),
                create("Налоги", "💵", TransactionType.EXPENSE, user, parentMap.get("Финансовые обязательства"))
        );
        categoryRepository.saveAll(categories);
    }

    private Category create(String name, String icon, TransactionType type, User user, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setType(type);
        category.setUser(user);
        category.setParentCategory(parent);
        return category;
    }
}
