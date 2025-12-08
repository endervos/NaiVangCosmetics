package demoweb.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BestSellerService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTop10BestsellersByCategory(Integer categoryId) {
        List<Map<String, Object>> results = orderService.getTop10BestsellersByCategory(categoryId);
        List<Map<String, Object>> bestsellers = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("#,###");
        for (Map<String, Object> result : results) {
            Map<String, Object> item = new HashMap<>();
            Integer itemId = (Integer) result.get("itemId");
            Double avgRating = reviewService.getAverageRating(itemId);
            int reviewCount = reviewService.getReviewCount(itemId);
            item.put("itemId", itemId);
            item.put("name", result.get("itemName"));
            item.put("categoryId", result.get("categoryId"));
            item.put("categoryName", result.get("categoryName"));
            item.put("totalSold", result.get("totalSold"));
            Object priceObj = result.get("price");
            String formattedPrice;
            if (priceObj instanceof Number) {
                formattedPrice = formatter.format(((Number) priceObj).longValue());
            } else if (priceObj instanceof String) {
                formattedPrice = (String) priceObj;
            } else {
                formattedPrice = "0";
            }
            item.put("price", formattedPrice);
            item.put("averageRating", avgRating != null ? avgRating : 0.0);
            item.put("reviewCount", reviewCount);
            bestsellers.add(item);
        }
        return bestsellers;
    }
}