package demoweb.demo.service;

import demoweb.demo.repository.VoucherCustomerItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlashSaleService {

    @Autowired
    private VoucherCustomerItemRepository voucherCustomerItemRepository;

    @Autowired
    private ReviewService reviewService;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTop5FlashSaleItems() {
        List<Object[]> results = voucherCustomerItemRepository.findTop5FlashSaleItems();
        List<Map<String, Object>> flashSaleItems = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("#,###");
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            Integer itemId = (Integer) row[0];
            Integer price = (Integer) row[2];
            Integer discountPercent = ((Number) row[5]).intValue();
            Integer finalPrice = ((Number) row[7]).intValue();
            Double avgRating = reviewService.getAverageRating(itemId);
            int reviewCount = reviewService.getReviewCount(itemId);
            item.put("itemId", itemId);
            item.put("name", row[1]);
            item.put("color", row[4]);
            item.put("price", formatter.format(price));
            item.put("discountPercent", discountPercent);
            item.put("discountAmount", row[6]);
            item.put("finalPrice", formatter.format(finalPrice));
            item.put("averageRating", avgRating != null ? avgRating : 0.0);
            item.put("reviewCount", reviewCount);
            flashSaleItems.add(item);
        }
        return flashSaleItems;
    }
}