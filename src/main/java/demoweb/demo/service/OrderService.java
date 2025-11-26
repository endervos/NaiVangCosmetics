package demoweb.demo.service;

import demoweb.demo.entity.*;
import demoweb.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    public List<Order> getOrdersByCustomer(Integer customerId){
        return orderRepository.findByCustomer_CustomerId(customerId);
    }

    public List<Order> getOrdersByStatus(Integer customerId, OrderStatus status){
        return orderRepository.findByCustomer_CustomerIdAndStatus(customerId, status);
    }

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }


    @Autowired
    private PaymentRepository paymentRepository;

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


}
