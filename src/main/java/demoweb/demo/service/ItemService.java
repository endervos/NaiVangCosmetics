package demoweb.demo.service;

import demoweb.demo.entity.Item;
import demoweb.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> getItemByID(Integer itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
