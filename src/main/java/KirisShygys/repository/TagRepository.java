package KirisShygys.repository;

import KirisShygys.entity.Tag;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUser(@Param("user") User user);
    Optional<Tag> findByIdAndUser(Long id, User user);
}
