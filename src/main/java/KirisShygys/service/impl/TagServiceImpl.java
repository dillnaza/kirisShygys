package KirisShygys.service.impl;

import KirisShygys.entity.Tag;
import KirisShygys.repository.TagRepository;
import KirisShygys.repository.UserRepository;
import KirisShygys.service.TagService;
import KirisShygys.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends TransactionEntityService<Tag, Long> implements TagService {

    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        super(tagRepository, "Tag", userRepository, jwtUtil);
    }
}
