package demoweb.demo.repository;

import demoweb.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

    List<Item> findByCategory_CategoryId(Integer categoryId);

    List<Item> findByNameContainingIgnoreCase(String name);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.images WHERE i.category.categoryId = :categoryId")
    List<Item> findByCategoryIdWithImages(@Param("categoryId") Integer categoryId);
}
