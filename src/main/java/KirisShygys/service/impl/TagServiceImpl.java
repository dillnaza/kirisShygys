package KirisShygys.service.impl;

import KirisShygys.entity.Tag;
import KirisShygys.entity.User;
import KirisShygys.exception.NotFoundException;
import KirisShygys.repository.TagRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.TagService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagServiceImpl extends TransactionEntityService<Tag, Long> implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        super(tagRepository, "Tag", userRepository, jwtUtil);
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Tag create(String token, Tag tag) {
        User user = getAuthenticatedUser(token);
        tag.setUser(user);
        return super.create(token, tag);
    }
}
