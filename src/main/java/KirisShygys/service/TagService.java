package KirisShygys.service;

import KirisShygys.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getTags(String token);
    Tag createTag(String token, Tag tag);
    Tag updateTag(String token, Long id, Tag updatedTag);
    void deleteTag(String token, Long id);
}