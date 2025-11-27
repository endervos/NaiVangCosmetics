package demoweb.demo.service;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.ItemImage;
import demoweb.demo.repository.ItemRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ReviewService reviewService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemImageService itemImageService;

    @Autowired
    public ItemService(ItemRepository itemRepository, ReviewService reviewService) {
        this.itemRepository = itemRepository;
        this.reviewService = reviewService;
    }

    private void attachRating(Item item) {
        if (item.getItemId() == null) return;

        Double avg = reviewService.getAverageRating(item.getItemId());
        int count = reviewService.getReviewCount(item.getItemId());

        if (avg == null) avg = 0.0;

        item.setAverageRating(avg);
        item.setReviewCount(count);
        item.setRatingStars(reviewService.getRatingStars(avg));
    }

    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll();
        items.forEach(this::attachRating);
        return items;
    }

    public Optional<Item> getItemById(Integer itemId) {
        Optional<Item> itemOpt = itemRepository.findByIdWithImages(itemId);
        itemOpt.ifPresent(this::attachRating);
        return itemOpt;
    }

    public List<Item> getItemsByCategoryIdWithFullData(Integer categoryId) {
        List<Item> items = itemRepository.findByCategory_CategoryId(categoryId);
        items.forEach(this::attachRating);
        return items;
    }

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

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void delete(Integer itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    public Item getItemDetail(Integer id) {
        Item item = itemRepository.findByIdWithFullData(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        item.getImages().size();
        if (item.getCategory() != null) {
            item.getCategory().getName();
        }

        attachRating(item);
        return item;
    }

    @Transactional
    public Item createItem(String name, String description, String color,
                           String ingredient, Integer price, Integer categoryId,
                           MultipartFile imageFile) throws IOException {

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setColor(color);
        item.setIngredient(ingredient);
        item.setPrice(price);

        Category category = categoryService.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        item.setCategory(category);

        Item savedItem = itemRepository.save(item);

        if (imageFile != null && !imageFile.isEmpty()) {
            ItemImage newImage = new ItemImage();
            newImage.setImageBlob(imageFile.getBytes());
            newImage.setIsPrimary(true);
            newImage.setAlt(imageFile.getOriginalFilename());
            itemImageService.saveForItem(savedItem.getItemId(), newImage);
        }

        return itemRepository.findByIdWithFullData(savedItem.getItemId())
                .orElse(savedItem);
    }

    @Transactional
    public Item updateItem(Integer id, String name, String description, String color,
                           String ingredient, Integer price, Integer categoryId,
                           MultipartFile imageFile) throws IOException {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        item.setName(name);
        item.setDescription(description);
        item.setColor(color);
        item.setIngredient(ingredient);
        item.setPrice(price);

        Category category = categoryService.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        item.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            List<ItemImage> oldImages = itemImageService.getImagesByItemId(id);
            oldImages.forEach(img -> {
                if (Boolean.TRUE.equals(img.getIsPrimary())) {
                    itemImageService.delete(img.getItemImageId());
                }
            });

            ItemImage newImage = new ItemImage();
            newImage.setImageBlob(imageFile.getBytes());
            newImage.setIsPrimary(true);
            newImage.setAlt(imageFile.getOriginalFilename());
            itemImageService.saveForItem(id, newImage);
        }

        Item savedItem = itemRepository.save(item);

        return itemRepository.findByIdWithFullData(id)
                .orElse(savedItem);
    }
}