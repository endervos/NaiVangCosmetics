package demoweb.demo.repository;

import demoweb.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Lấy tất cả category con theo id cha
    List<Category> findByParent_CategoryId(Integer parentId);

    // Lấy tất cả category gốc (parent = null)
    List<Category> findByParentIsNull();

    // Lấy category theo slug (SEO)
    Optional<Category> findBySlug(String slug);

    // Tìm category theo tên (không phân biệt hoa thường)
    List<Category> findByNameContainingIgnoreCase(String keyword);
}
