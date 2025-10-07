package demoweb.demo.repository;

import demoweb.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

    // Lấy item theo categoryA_id
    List<Item> findByCategory_CategoryId(Integer categoryId);

    // Tìm item theo tên (không phân biệt hoa/thường)
    List<Item> findByNameContainingIgnoreCase(String name);

    // Lấy item theo category kèm theo ảnh (tránh lỗi LazyInitializationException)
    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.images WHERE i.category.categoryId = :categoryId")
    List<Item> findByCategoryIdWithImages(@Param("categoryId") Integer categoryId);

    // ✅ Lấy item theo ID kèm theo ảnh (cho trang chi tiết)
    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.images WHERE i.itemId = :id")
    Optional<Item> findByIdWithImages(@Param("id") Integer id);

}
