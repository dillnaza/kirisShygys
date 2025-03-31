package KirisShygys.controller;

import KirisShygys.dto.CategoryRequest;
import KirisShygys.entity.Category;
import KirisShygys.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private String getAuthToken(HttpServletRequest request) {
        return (String) request.getAttribute("AuthToken");
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(HttpServletRequest request) {
        return ResponseEntity.ok(categoryService.getCategories(getAuthToken(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(HttpServletRequest request,
                                                    @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(getAuthToken(request), id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(HttpServletRequest request,
                                                   @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.createCategory(getAuthToken(request), categoryRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(HttpServletRequest request,
                                                   @PathVariable Long id,
                                                   @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(getAuthToken(request), id, categoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(HttpServletRequest request,
                                               @PathVariable Long id) {
        categoryService.deleteCategory(getAuthToken(request), id);
        return ResponseEntity.noContent().build();
    }
}