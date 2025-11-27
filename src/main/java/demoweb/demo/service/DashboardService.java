package demoweb.demo.service;

import demoweb.demo.entity.Order;
import demoweb.demo.repository.OrderRepository;
import demoweb.demo.repository.ItemRepository;
import demoweb.demo.repository.CustomerRepository;
import demoweb.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public DashboardService(OrderRepository orderRepository,
                            ItemRepository itemRepository,
                            CustomerRepository customerRepository,
                            CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalProducts = itemRepository.count();
        stats.put("totalProducts", totalProducts);

        long totalCustomers = customerRepository.count();
        stats.put("totalCustomers", totalCustomers);

        long totalOrders = orderRepository.count();
        stats.put("totalOrders", totalOrders);

        // Doanh thu tháng này
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Order> ordersThisMonth = orderRepository.findByPlacedAtBetween(startOfMonth, endOfMonth);
        long monthlyRevenue = ordersThisMonth.stream()
                .mapToLong(order -> order.getTotal() != null ? order.getTotal() : 0L)
                .sum();
        stats.put("monthlyRevenue", monthlyRevenue);

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRevenueChart() {
        List<Map<String, Object>> revenueData = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime startDate = month.atDay(1).atStartOfDay();
            LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59);

            List<Order> ordersInMonth = orderRepository.findByPlacedAtBetween(startDate, endDate);
            long revenue = ordersInMonth.stream()
                    .mapToLong(order -> order.getTotal() != null ? order.getTotal() : 0L)
                    .sum();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month.getMonth().getValue());
            monthData.put("year", month.getYear());
            monthData.put("label", "T" + month.getMonthValue() + "/" + month.getYear());
            monthData.put("revenue", revenue);

            revenueData.add(monthData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", revenueData);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCategoryDistribution() {
        List<Object[]> categoryStats = categoryRepository.findCategoryItemCounts();

        List<Map<String, Object>> categoryData = new ArrayList<>();
        for (Object[] stat : categoryStats) {
            String categoryName = (String) stat[0];
            Long itemCount = (Long) stat[1];

            Map<String, Object> data = new HashMap<>();
            data.put("category", categoryName);
            data.put("count", itemCount);
            categoryData.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", categoryData);
        return result;
    }
}