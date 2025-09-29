package demoweb.demo.repository;

import demoweb.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findById(Integer itemId);
}
