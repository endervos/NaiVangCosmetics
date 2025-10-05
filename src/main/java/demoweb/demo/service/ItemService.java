package demoweb.demo.service;

import demoweb.demo.entity.Item;
import demoweb.demo.repository.ItemRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ReviewService reviewService;

    @Autowired
    public ItemService(ItemRepository itemRepository, ReviewService reviewService) {
        this.itemRepository = itemRepository;
        this.reviewService = reviewService;
    }

    /** ✅ Gắn rating cho 1 item */
    private void attachRating(Item item) {
        if (item.getItemId() == null) return;

        Double avg = reviewService.getAverageRating(item.getItemId());
        int count = reviewService.getReviewCount(item.getItemId());

        if (avg == null) avg = 0.0;

        item.setAverageRating(avg);
        item.setReviewCount(count);
        item.setRatingStars(reviewService.getRatingStars(avg));
    }

    /** ✅ Lấy tất cả item có rating */
    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll();
        items.forEach(this::attachRating);
        return items;
    }

    /** ✅ Lấy item theo ID (kèm rating) */
    public Optional<Item> getItemById(Integer itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        itemOpt.ifPresent(this::attachRating);
        return itemOpt;
    }

    /** ✅ Lấy item theo category, kèm rating (fetch thật như filter) */
    public List<Item> getItemsByCategoryIdWithFullData(Integer categoryId) {
        List<Item> items = itemRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        items.forEach(this::attachRating);
        return items;
    }

    /** ✅ Lọc item theo khoảng giá và category (vẫn kèm rating) */
    public List<Item> filterByPrice(Integer categoryId, Integer min, Integer max) {
        List<Item> items = itemRepository.findAll((root, query, cb) -> {
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

        items.forEach(this::attachRating);
        return items;
    }

    /** ✅ CRUD cơ bản */
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void delete(Integer itemId) {
        itemRepository.deleteById(itemId);
    }
}
