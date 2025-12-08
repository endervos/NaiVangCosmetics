package demoweb.demo.service;

import demoweb.demo.repository.OrderItemRepository;
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
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReviewService reviewService;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTop10BestsellersByCategory(Integer categoryId) {
        List<Object[]> results = orderItemRepository.findTop10BestsellersByCategory(categoryId);
        List<Map<String, Object>> bestsellers = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("#,###");
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            Integer itemId = (Integer) row[0];
            Integer price = (Integer) row[2];
            Double avgRating = reviewService.getAverageRating(itemId);
            int reviewCount = reviewService.getReviewCount(itemId);
            item.put("itemId", itemId);
            item.put("name", row[1]);
            item.put("price", formatter.format(price));
            item.put("description", row[3]);
            item.put("color", row[4]);
            item.put("totalSold", row[5]);
            item.put("averageRating", avgRating != null ? avgRating : 0.0);
            item.put("reviewCount", reviewCount);
            bestsellers.add(item);
        }
        return bestsellers;
    }
}