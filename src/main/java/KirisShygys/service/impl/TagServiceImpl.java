package KirisShygys.service.impl;

import KirisShygys.entity.Tag;
import KirisShygys.entity.User;
import KirisShygys.exception.ForbiddenException;
import KirisShygys.exception.NotFoundException;
import KirisShygys.exception.UnauthorizedException;
import KirisShygys.repository.TagRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.TagService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.tagRepository = tagRepository;
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
    public List<Tag> getTags(String token) {
        User user = getAuthenticatedUser(token);
        return tagRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Tag createTag(String token, Tag tag) {
        User user = getAuthenticatedUser(token);
        tag.setUser(user);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(String token, Long id, Tag updatedTag) {
        User user = getAuthenticatedUser(token);
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + id + " not found"));

        if (!tag.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You do not have permission to modify this tag");
        }

        tag.setName(updatedTag.getName());
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(String token, Long id) {
        User user = getAuthenticatedUser(token);
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag with ID " + id + " not found"));

        if (!tag.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You do not have permission to delete this tag");
        }

        tagRepository.delete(tag);
    }
}
