package demoweb.demo.service;

import demoweb.demo.entity.Category;
import demoweb.demo.entity.Item;
import demoweb.demo.entity.ItemImage;
import demoweb.demo.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
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
    private EntityManager entityManager;

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
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetItemByIdWithImages");
        query.registerStoredProcedureParameter(1, Integer.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, itemId);
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        if (rawResults.isEmpty()) {
            return Optional.empty();
        }
        Item item = null;
        List<ItemImage> images = new ArrayList<>();
        for (Object[] row : rawResults) {
            if (item == null) {
                item = new Item();
                item.setItemId((Integer) row[0]);
                item.setName((String) row[1]);
                item.setDescription((String) row[2]);
                item.setColor((String) row[3]);
                item.setIngredient((String) row[4]);
                item.setPrice((Integer) row[5]);
                if (row[8] != null) {
                    java.sql.Timestamp createdTimestamp = (java.sql.Timestamp) row[8];
                    item.setCreatedAt(createdTimestamp.toLocalDateTime());
                }
                if (row[9] != null) {
                    java.sql.Timestamp updatedTimestamp = (java.sql.Timestamp) row[9];
                    item.setUpdatedAt(updatedTimestamp.toLocalDateTime());
                }
            }
            if (row[11] != null) {
                ItemImage image = new ItemImage();
                image.setItemImageId((Integer) row[11]);
                image.setImageBlob((byte[]) row[12]);
                image.setAlt((String) row[13]);
                image.setIsPrimary((Boolean) row[14]);
                if (row[15] != null) {
                    java.sql.Timestamp imgTimestamp = (java.sql.Timestamp) row[15];
                    image.setCreatedAt(imgTimestamp.toLocalDateTime());
                }
                image.setItem(item);
                images.add(image);
            }
        }
        if (item != null) {
            item.setImages(images);
            attachRating(item);
        }
        return Optional.ofNullable(item);
    }

    public List<Item> getItemsByCategoryIdWithFullData(Integer categoryId) {
        List<Item> items = itemRepository.findByCategory_CategoryId(categoryId);
        items.forEach(this::attachRating);
        return items;
    }

    @Transactional(readOnly = true)
    public List<Item> searchByName(String keyword) {
        List<Item> items = itemRepository.findByNameContainingIgnoreCase(keyword);
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
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetItemByIdWithFullData");
        query.registerStoredProcedureParameter(1, Integer.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter(1, id);
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        if (rawResults.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        Item item = null;
        List<ItemImage> images = new ArrayList<>();
        for (Object[] row : rawResults) {
            if (item == null) {
                item = new Item();
                item.setItemId((Integer) row[0]);
                item.setName((String) row[1]);
                item.setDescription((String) row[2]);
                item.setColor((String) row[3]);
                item.setIngredient((String) row[4]);
                item.setPrice((Integer) row[5]);
                if (row[8] != null) {
                    java.sql.Timestamp createdTimestamp = (java.sql.Timestamp) row[8];
                    item.setCreatedAt(createdTimestamp.toLocalDateTime());
                }
                if (row[9] != null) {
                    java.sql.Timestamp updatedTimestamp = (java.sql.Timestamp) row[9];
                    item.setUpdatedAt(updatedTimestamp.toLocalDateTime());
                }
                if (row[11] != null) {
                    Category category = new Category();
                    category.setCategoryId((Integer) row[11]);
                    category.setName((String) row[12]);
                    category.setSlug((String) row[13]);
                    item.setCategory(category);
                }
            }
            if (row[15] != null) {
                ItemImage image = new ItemImage();
                image.setItemImageId((Integer) row[15]);
                image.setImageBlob((byte[]) row[16]);
                image.setAlt((String) row[17]);
                image.setIsPrimary((Boolean) row[18]);
                if (row[19] != null) {
                    java.sql.Timestamp imgTimestamp = (java.sql.Timestamp) row[19];
                    image.setCreatedAt(imgTimestamp.toLocalDateTime());
                }
                image.setItem(item);
                images.add(image);
            }
        }
        if (item != null) {
            item.setImages(images);
            attachRating(item);
        }
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
        return getItemDetail(savedItem.getItemId());
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
        itemRepository.save(item);
        return getItemDetail(id);
    }

//    @Transactional(readOnly = true)
    @Transactional
    public List<Item> getTop5LatestItems() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetTop5LatestItems", Item.class);
        @SuppressWarnings("unchecked")
        List<Item> items = query.getResultList();
        items = items.size() > 5 ? items.subList(0, 5) : items;
        items.forEach(this::attachRating);
        return items;
    }
}