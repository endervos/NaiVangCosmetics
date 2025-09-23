// Đối tượng chứa thông tin đơn hàng
const orderData = {
    orderId: "#250909524759",
    orderDate: "09/09/2025, 15:29",
    deliveryAddress: "hiin- 0123456789, 97, Man Thiện, Phường Hiệp Phú, Quận 9, Hồ Chí Minh",
    paymentMethod: "Thanh toán khi nhận hàng",
    subtotal: "232.000 đ",
    discount: "0 đ",
    shippingFee: "0 đ",
    total: "232.000 đ",
    product: {
        image: "/src/main/resources/static/DetailOrder/Image/Item1.png",
        name: "Cocoon Combo 2 Nước Tẩy Trang Bioderma Cocoon Làm Sạch & Giảm Dầu 500ml (2x500ml)",
        quantity: 1,
        price: "232.000 đ"
    }
};

// Hàm cập nhật dữ liệu động vào giao diện
function updateOrderDetails(orderData) {
    document.getElementById("order-title").textContent = `Chi tiết đơn hàng ${orderData.orderId}`;
    document.getElementById("order-date").textContent = `Ngày đặt: ${orderData.orderDate}`;
    
    // Cập nhật thông tin nhận hàng
    document.getElementById("delivery-address-value").textContent = orderData.deliveryAddress;
    
    // Cập nhật phương thức thanh toán
    document.getElementById("payment-method-value").textContent = orderData.paymentMethod;
    
    // Cập nhật tạm tính, giảm giá, phí vận chuyển, và tổng tiền
    document.getElementById("subtotal-value").textContent = orderData.subtotal;
    document.getElementById("discount-value").textContent = orderData.discount;
    document.getElementById("shipping-fee-value").textContent = orderData.shippingFee;
    document.getElementById("total-value").textContent = orderData.total;
    
    // Cập nhật thông tin sản phẩm
    document.getElementById("product-image").src = orderData.product.image;
    document.getElementById("product-name").textContent = orderData.product.name;
    document.getElementById("product-quantity").textContent = `Số lượng: ${orderData.product.quantity}`;
    document.getElementById("product-price").textContent = orderData.product.price;
}

// Gọi hàm cập nhật thông tin đơn hàng khi trang tải xong
document.addEventListener("DOMContentLoaded", function() {
    updateOrderDetails(orderData);
});

// Hàm mở cửa sổ đánh giá
function openReview() {
    alert("Mở cửa sổ đánh giá cho sản phẩm");
}

// Hàm quay lại trang trước
function goBack() {
    window.history.back(); // Quay lại trang trước trong lịch sử trình duyệt
}
