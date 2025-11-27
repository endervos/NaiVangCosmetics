package demoweb.demo.repository;

import demoweb.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByParent_CategoryId(Integer parentId);

    List<Category> findByParentIsNull();

    Optional<Category> findBySlug(String slug);

    List<Category> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.children " +
            "WHERE c.parent IS NULL")
    List<Category> findRootCategoriesWithChildren();
}
