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

    List<Item> findByCategory_CategoryId(Integer categoryId);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.images WHERE i.itemId = :id")
    Optional<Item> findByIdWithImages(@Param("id") Integer id);

    @Query("SELECT DISTINCT i FROM Item i " +
            "LEFT JOIN FETCH i.images " +
            "LEFT JOIN FETCH i.category " +
            "WHERE i.itemId = :id")
    Optional<Item> findByIdWithFullData(@Param("id") Integer id);
}