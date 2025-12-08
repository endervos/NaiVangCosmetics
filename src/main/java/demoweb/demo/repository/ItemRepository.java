package demoweb.demo.repository;

import demoweb.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

    List<Item> findByCategory_CategoryId(Integer categoryId);

    List<Item> findByNameContainingIgnoreCase(String name);
}