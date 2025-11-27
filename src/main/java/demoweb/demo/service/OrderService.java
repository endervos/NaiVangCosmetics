package demoweb.demo.service;

import demoweb.demo.entity.*;
import demoweb.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final VoucherRepository voucherRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        AddressRepository addressRepository,
                        VoucherRepository voucherRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.addressRepository = addressRepository;
        this.voucherRepository = voucherRepository;
    }

    public List<Order> getOrdersByCustomer(Integer customerId){
        return orderRepository.findByCustomer_CustomerId(customerId);
    }

    public List<Order> getOrdersByStatus(Integer customerId, OrderStatus status){
        return orderRepository.findByCustomer_CustomerIdAndStatus(customerId, status);
    }

    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }

    public Optional<Payment> getPaymentByOrderId(Integer orderId) {
        return paymentRepository.findByOrder_OrderId(orderId);
    }

    public Optional<Address> getAddressByOrderId(Integer addressId) {
        return addressRepository.findById(addressId);
    }

    public Optional<Voucher> getVoucherById(Integer voucherId) {
        if (voucherId == null) return Optional.empty();
        return voucherRepository.findById(voucherId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            List<Map<String, Object>> result = orders.stream()
                    .map(this::convertOrderToMap)
                    .collect(Collectors.toList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    private Map<String, Object> convertOrderToMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", order.getOrderId());
        map.put("total", order.getTotal());
        map.put("status", order.getStatus().name());
        map.put("placedAt", order.getPlacedAt());
        map.put("updatedAt", order.getUpdatedAt());
        if (order.getCustomer() != null) {
            map.put("customerId", order.getCustomer().getCustomerId());
            if (order.getCustomer().getUser() != null) {
                map.put("customerName", order.getCustomer().getUser().getFullname());
                map.put("customerEmail", order.getCustomer().getUser().getEmail());
            }
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderDetailForManager(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(orderId);
        Optional<Payment> paymentOpt = paymentRepository.findByOrder_OrderId(orderId);

        Optional<Address> addressOpt = Optional.empty();
        if (order.getAddress() != null) {
            addressOpt = addressRepository.findById(order.getAddress().getAddressId());
        }

        Optional<Voucher> voucherOpt = Optional.empty();
        if (order.getVoucher() != null) {
            voucherOpt = voucherRepository.findById(order.getVoucher().getVoucherId());
        }
        long giamGia = voucherOpt
                .map(v -> order.getTotal() * v.getDiscountPercent() / 100)
                .orElse(0L);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("orderId", order.getOrderId());
        orderMap.put("total", order.getTotal());
        orderMap.put("status", order.getStatus().name());
        orderMap.put("placedAt", order.getPlacedAt());
        orderMap.put("updatedAt", order.getUpdatedAt());
        result.put("order", orderMap);
        List<Map<String, Object>> orderItemMaps = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("orderItemId", orderItem.getOrderItemId());
            itemMap.put("quantity", orderItem.getQuantity());
            itemMap.put("preDiscountPrice", orderItem.getPreDiscountPrice());
            itemMap.put("totalPriceCents", orderItem.getTotalPriceCents());
            if (orderItem.getItem() != null) {
                Item item = orderItem.getItem();
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("itemId", item.getItemId());
                productMap.put("name", item.getName());
                if (item.getImages() != null && !item.getImages().isEmpty()) {
                    productMap.put("imageUrl", "/api/item-images/blob/" + item.getImages().get(0).getItemImageId());
                } else {
                    productMap.put("imageUrl", "/Customer/Example/Image/default-product.jpg");
                }
                productMap.put("price", item.getPrice());
                itemMap.put("item", productMap);
            }
            orderItemMaps.add(itemMap);
        }
        result.put("orderItems", orderItemMaps);

        if (paymentOpt.isPresent()) {
            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("paymentMethod", paymentOpt.get().getPaymentMethod());
            paymentMap.put("paymentStatus", paymentOpt.get().getStatus());
            result.put("payment", paymentMap);
        } else {
            result.put("payment", null);
        }
        if (addressOpt.isPresent()) {
            Address addr = addressOpt.get();
            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put("addressId", addr.getAddressId());
            addressMap.put("street", addr.getStreet());
            addressMap.put("district", addr.getDistrict());
            addressMap.put("city", addr.getCity());
            addressMap.put("phoneNumber", addr.getPhoneNumber());
            result.put("address", addressMap);
        } else {
            result.put("address", null);
        }
        if (voucherOpt.isPresent()) {
            Voucher v = voucherOpt.get();
            Map<String, Object> voucherMap = new HashMap<>();
            voucherMap.put("voucherId", v.getVoucherId());
            voucherMap.put("code", v.getCode());
            voucherMap.put("description", v.getDescription());
            voucherMap.put("discountPercent", v.getDiscountPercent());
            result.put("voucher", voucherMap);
        } else {
            result.put("voucher", null);
        }
        result.put("giamGia", giamGia);
        if (order.getCustomer() != null && order.getCustomer().getUser() != null) {
            result.put("customerName", order.getCustomer().getUser().getFullname());
            result.put("customerEmail", order.getCustomer().getUser().getEmail());
        }
        return result;
    }

    @Transactional
    public Order updateOrderStatus(Integer orderId, String statusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
            order.setStatus(newStatus);
            Order savedOrder = orderRepository.save(order);
            return savedOrder;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + statusStr);
        }
    }
}