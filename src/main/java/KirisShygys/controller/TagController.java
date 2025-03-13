package KirisShygys.controller;

import KirisShygys.entity.Tag;
import KirisShygys.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(tagService.getAll(token.replace("Bearer ", "")));
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestHeader("Authorization") String token, @RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.create(token.replace("Bearer ", ""), tag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Tag updatedTag) {
        return ResponseEntity.ok(tagService.update(token.replace("Bearer ", ""), id, updatedTag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        tagService.delete(token.replace("Bearer ", ""), id);
        return ResponseEntity.noContent().build();
    }
}
