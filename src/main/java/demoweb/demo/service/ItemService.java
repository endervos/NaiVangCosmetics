package demoweb.demo.service;

import demoweb.demo.entity.Item;
import demoweb.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Lấy item theo id
    public Optional<Item> getItemById(Integer itemId) {
        return itemRepository.findById(itemId);
    }

    // Lấy tất cả item
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Thêm hoặc update item
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    // Xóa item
    public void delete(Integer itemId) {
        itemRepository.deleteById(itemId);
    }

    public List<Item> filterByPrice(Integer categoryId, Integer min, Integer max) {
        return itemRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }
            if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            }
            if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), max));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

}
