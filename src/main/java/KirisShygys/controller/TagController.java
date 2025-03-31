package KirisShygys.controller;

import KirisShygys.entity.Tag;
import KirisShygys.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    private String getAuthToken(HttpServletRequest request) {
        return (String) request.getAttribute("AuthToken");
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags(HttpServletRequest request) {
        return ResponseEntity.ok(tagService.getAll(getAuthToken(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(HttpServletRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(getAuthToken(request), id));
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(HttpServletRequest request, @RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.create(getAuthToken(request), tag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(HttpServletRequest request, @PathVariable Long id, @RequestBody Tag updatedTag) {
        return ResponseEntity.ok(tagService.update(getAuthToken(request), id, updatedTag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(HttpServletRequest request, @PathVariable Long id) {
        tagService.delete(getAuthToken(request), id);
        return ResponseEntity.noContent().build();
    }
}
