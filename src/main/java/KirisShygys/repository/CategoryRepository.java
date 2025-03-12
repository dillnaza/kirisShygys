package KirisShygys.repository;

import KirisShygys.entity.Category;
import KirisShygys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    Optional<Category> findByCategoryIdAndUser(Long id, User user);
    List<Category> findByUserAndParentCategoryIsNull(User user);

}
