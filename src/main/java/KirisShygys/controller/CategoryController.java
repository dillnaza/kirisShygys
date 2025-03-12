package KirisShygys.controller;

import KirisShygys.entity.Category;
import KirisShygys.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(categoryService.getCategories(token.replace("Bearer ", "")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(token.replace("Bearer ", ""), id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestHeader("Authorization") String token, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(token.replace("Bearer ", ""), category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(token.replace("Bearer ", ""), id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        categoryService.deleteCategory(token.replace("Bearer ", ""), id);
        return ResponseEntity.noContent().build();
    }
}
