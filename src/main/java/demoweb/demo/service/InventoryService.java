package demoweb.demo.service;

import java.time.LocalDateTime;

import demoweb.demo.entity.Inventory;
import demoweb.demo.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void reserveItem(Integer itemId, int quantity) {
        Inventory inv = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy inventory cho item ID=" + itemId));

        inv.setReserved(inv.getReserved() + quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    @Transactional
    public void confirmOrder(Integer itemId, int quantity) {
        Inventory inv = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy inventory cho item ID=" + itemId));

        if (inv.getReserved() < quantity)
            throw new RuntimeException("Không đủ hàng reserved để xác nhận đơn!");

        inv.setReserved(inv.getReserved() - quantity);
        inv.setQuantity(inv.getQuantity() - quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    @Transactional
    public void releaseReserved(Integer itemId, int quantity) {
        Inventory inv = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy inventory cho item ID=" + itemId));

        int newReserved = Math.max(inv.getReserved() - quantity, 0);
        inv.setReserved(newReserved);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    @Transactional
    public void updateStock(Integer itemId, int newQuantity) {
        Inventory inv = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy inventory cho item ID=" + itemId));
        inv.setQuantity(newQuantity);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }
}