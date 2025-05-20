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
                .filter(category -> !category.isDeleted())
                .toList();
    }

    @Override
    public Category getCategoryById(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));
        if (category.isDeleted()) {
            throw new NotFoundException("Category with ID " + id + " not found");
        }
        return category;
    }

    @Transactional
    public Category createCategory(String token, CategoryRequest request) {
        User user = getAuthenticatedUser(token);
        Category category = new Category();
        category.setNameRu(request.getNameRu());
        category.setNameKz(request.getNameKz());
        category.setNameEn(request.getNameEn());
        category.setIcon(request.getIcon());
        category.setType(request.getType());
        category.setUser(user);
        category.setSystem(false);
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
        if (existingCategory.isSystem()) {
            throw new UnsupportedOperationException("Category 'Without category' cannot be edited");
        }
        existingCategory.setNameRu(request.getNameRu());
        existingCategory.setNameKz(request.getNameKz());
        existingCategory.setNameEn(request.getNameEn());
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
        if (category.isSystem()) {
            throw new UnsupportedOperationException("Cannot remove the 'Uncategorized' category");
        }
        categoryRepository.findByUser(user).stream()
                .filter(c -> category.equals(c.getParentCategory()))
                .forEach(sub -> {
                    sub.setDeleted(true);
                    categoryRepository.save(sub);
                });
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void createDefaultCategories(User user) {
        categoryRepository.save(create("Без категории", "Санатсыз", "Uncategorized", "🚫", TransactionType.EXPENSE, user, null, true));
        Map<String, Category> parentMap = new HashMap<>();
        List<Category> parents = List.of(
                create("Государственные выплаты", "Мемлекеттік төлемдер", "Government Payments", "🏛️", TransactionType.INCOME, user, null, false),
                create("Возвраты", "Қайтарымдар", "Returns", "🔄", TransactionType.INCOME, user, null, false),
                create("Транспорт", "Көлік", "Transport", "🚗", TransactionType.EXPENSE, user, null, false),
                create("Финансовые обязательства", "Қаржылық міндеттемелер", "Financial Obligations", "💳", TransactionType.EXPENSE, user, null, false)
        );
        for (Category parent : parents) {
            categoryRepository.save(parent);
            parentMap.put(parent.getNameRu(), parent); // ключ - nameRu
        }
        List<Category> categories = List.of(
                create("Зарплата", "Жалақы", "Salary", "💼", TransactionType.INCOME, user, null, false),
                create("Стипендия", "Стипендия", "Scholarship", "🎓", TransactionType.INCOME, user, parentMap.get("Государственные выплаты"), false),
                create("Пособие", "Жәрдемақы", "Benefit", "📩", TransactionType.INCOME, user, parentMap.get("Государственные выплаты"), false),
                create("Подарки", "Сыйлықтар", "Gifts", "🎁", TransactionType.INCOME, user, null, false),
                create("Пассивный доход", "Пассив табыс", "Passive Income", "🏠", TransactionType.INCOME, user, null, false),
                create("Налоги", "Салықтар", "Taxes", "💸", TransactionType.INCOME, user, parentMap.get("Возвраты"), false),
                create("Долги", "Қарыздар", "Debts", "🔁", TransactionType.INCOME, user, parentMap.get("Возвраты"), false),
                create("Кэшбэк / Бонусы", "Кэшбэк / Бонустар", "Cashback / Bonuses", "🎉", TransactionType.INCOME, user, null, false),
                create("Продукты питания", "Азық-түлік", "Groceries", "🍎", TransactionType.EXPENSE, user, null, false),
                create("Аренда", "Жалдау", "Rent", "🏠", TransactionType.EXPENSE, user, null, false),
                create("Коммунальные услуги", "Коммуналдық қызметтер", "Utilities", "💡", TransactionType.EXPENSE, user, null, false),
                create("Мобильная связь / Интернет", "Мобайл / Интернет", "Mobile / Internet", "📶", TransactionType.EXPENSE, user, null, false),
                create("Здоровье", "Денсаулық", "Health", "💊", TransactionType.EXPENSE, user, null, false),
                create("Красота и уход", "Сұлулық және күтім", "Beauty & Care", "💅", TransactionType.EXPENSE, user, null, false),
                create("Образование / Курсы", "Білім / Курстар", "Education / Courses", "📚", TransactionType.EXPENSE, user, null, false),
                create("Подписки", "Жазылымдар", "Subscriptions", "📺", TransactionType.EXPENSE, user, null, false),
                create("Подарки другим", "Басқаларға сыйлықтар", "Gifts to Others", "🎁", TransactionType.EXPENSE, user, null, false),
                create("Такси", "Такси", "Taxi", "🚕", TransactionType.EXPENSE, user, parentMap.get("Транспорт"), false),
                create("Бензин", "Бензин", "Gasoline", "⛽", TransactionType.EXPENSE, user, parentMap.get("Транспорт"), false),
                create("Кредиты", "Несие", "Credits", "💳", TransactionType.EXPENSE, user, parentMap.get("Финансовые обязательства"), false),
                create("Долги", "Қарыздар", "Debts", "📉", TransactionType.EXPENSE, user, parentMap.get("Финансовые обязательства"), false),
                create("Налоги", "Салықтар", "Taxes", "💵", TransactionType.EXPENSE, user, parentMap.get("Финансовые обязательства"), false)
        );
        categoryRepository.saveAll(categories);
    }

    private Category create(String nameRu, String nameKz, String nameEn, String icon, TransactionType type, User user, Category parent, Boolean isSystem) {
        Category category = new Category();
        category.setNameRu(nameRu);
        category.setNameKz(nameKz);
        category.setNameEn(nameEn);
        category.setIcon(icon);
        category.setType(type);
        category.setUser(user);
        category.setParentCategory(parent);
        category.setSystem(isSystem);
        return category;
    }
}
