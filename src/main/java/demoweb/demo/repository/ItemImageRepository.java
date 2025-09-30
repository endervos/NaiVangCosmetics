package demoweb.demo.repository;

import demoweb.demo.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Integer> {

    List<ItemImage> findByItem_ItemId(Integer itemId);

    Optional<ItemImage> findByItem_ItemIdAndIsPrimaryTrue(Integer itemId);
}
