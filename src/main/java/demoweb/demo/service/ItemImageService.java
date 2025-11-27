package demoweb.demo.service;

import demoweb.demo.entity.Item;
import demoweb.demo.entity.ItemImage;
import demoweb.demo.repository.ItemImageRepository;
import demoweb.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemImageService {

    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemImageService(ItemImageRepository itemImageRepository, ItemRepository itemRepository) {
        this.itemImageRepository = itemImageRepository;
        this.itemRepository = itemRepository;
    }

    public Optional<ItemImage> findById(Integer imageId) {
        return itemImageRepository.findById(imageId);
    }

    public List<ItemImage> getAllImages() {
        return itemImageRepository.findAll();
    }

    public List<ItemImage> getImagesByItemId(Integer itemId) {
        return itemImageRepository.findByItem_ItemId(itemId);
    }
    public ItemImage getPrimaryImage(Integer itemId) {
        return itemImageRepository.findByItem_ItemIdAndIsPrimaryTrue(itemId)
                .orElseGet(() -> {
                    ItemImage placeholder = new ItemImage();
                    placeholder.setAlt("default image");
                    placeholder.setImageBlob(null);
                    return placeholder;
                });
    }
    public ItemImage saveForItem(Integer itemId, ItemImage itemImage) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id = " + itemId));

        itemImage.setItem(item);
        return itemImageRepository.save(itemImage);
    }

    public void delete(Integer imageId) {
        itemImageRepository.deleteById(imageId);
    }
}
