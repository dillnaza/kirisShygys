package KirisShygys.service.impl;

import KirisShygys.entity.Category;
import KirisShygys.entity.User;
import KirisShygys.exception.ForbiddenException;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private User getAuthenticatedUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Missing authentication token");
        }

        String email = jwtUtil.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token"));
    }

    @Override
    public List<Category> getCategories(String token) {
        User user = getAuthenticatedUser(token);
        return categoryRepository.findByUserAndParentCategoryIsNull(user);
    }

    @Override
    public Category getCategoryById(String token, Long id) {
        User user = getAuthenticatedUser(token);
        return categoryRepository.findByCategoryIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public Category createCategory(String token, Category category) {
        User user = getAuthenticatedUser(token);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(String token, Long id, Category updatedCategory) {
        User user = getAuthenticatedUser(token);
        Category category = categoryRepository.findByCategoryIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));

        if (!category.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You do not have permission to modify this category");
        }

        category.setName(updatedCategory.getName());
        category.setParentCategory(updatedCategory.getParentCategory());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Category category = categoryRepository.findByCategoryIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found"));

        if (!category.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You do not have permission to delete this category");
        }

        categoryRepository.delete(category);
    }
}