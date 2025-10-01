// Dữ liệu mẫu đơn hàng
let orders = [
    { id: 1, customerName: "Nguyễn Văn A", total: 1500000, status: "Đã giao", date: "2025-09-30 9:30", deliveryAddress: "123 Đường Láng, Hà Nội", paymentMethod: "Thanh toán khi nhận hàng", discount: 100000, shippingFee: 30000, items: [{ name: "Sản phẩm 1", quantity: 2, price: 500000, image: "/images/product1.jpg" }, { name: "Sản phẩm 2", quantity: 1, price: 500000, image: "/images/product2.jpg" }] },
    { id: 2, customerName: "Trần Thị B", total: 2500000, status: "Đang xử lý", date: "2025-09-29 16:12", deliveryAddress: "456 Nguyễn Trãi, TP.HCM", paymentMethod: "Chuyển khoản", discount: 0, shippingFee: 50000, items: [{ name: "Sản phẩm 3", quantity: 1, price: 2500000, image: "/images/product3.jpg" }] },
    { id: 3, customerName: "Lê Thị C", total: 800000, status: "Hủy", date: "2025-09-28 3:02", deliveryAddress: "789 Lê Lợi, Đà Nẵng", paymentMethod: "Thanh toán khi nhận hàng", discount: 50000, shippingFee: 40000, items: [{ name: "Sản phẩm 4", quantity: 4, price: 200000, image: "/images/product4.jpg" }] },
    { id: 4, customerName: "Vũ Minh D", total: 1200000, status: "Đang giao", date: "2025-09-27 19:05", deliveryAddress: "101 Trần Phú, Hải Phòng", paymentMethod: "Chuyển khoản", discount: 20000, shippingFee: 30000, items: [{ name: "Sản phẩm 5", quantity: 3, price: 400000, image: "/images/product5.jpg" }] },
    { id: 5, customerName: "Phạm Quỳnh E", total: 3000000, status: "Đã giao", date: "2025-09-26 20:10", deliveryAddress: "234 Nguyễn Huệ, Huế", paymentMethod: "Thanh toán khi nhận hàng", discount: 150000, shippingFee: 50000, items: [{ name: "Sản phẩm 6", quantity: 2, price: 1500000, image: "/images/product6.jpg" }] },
    { id: 6, customerName: "Đỗ Minh F", total: 950000, status: "Đang xử lý", date: "2025-09-25 12:30", deliveryAddress: "567 Lê Đại Hành, Hà Nội", paymentMethod: "Chuyển khoản", discount: 0, shippingFee: 30000, items: [{ name: "Sản phẩm 7", quantity: 5, price: 190000, image: "/images/product7.jpg" }] },
    { id: 7, customerName: "Hoàng Hải G", total: 2000000, status: "Hủy", date: "2025-09-24 9:12", deliveryAddress: "890 Đinh Tiên Hoàng, Đà Lạt", paymentMethod: "Thanh toán khi nhận hàng", discount: 100000, shippingFee: 40000, items: [{ name: "Sản phẩm 8", quantity: 2, price: 1000000, image: "/images/product8.jpg" }] },
    { id: 8, customerName: "Lê Thị H", total: 1800000, status: "Đã giao", date: "2025-09-23 3:50", deliveryAddress: "111 Phạm Văn Đồng, Cần Thơ", paymentMethod: "Chuyển khoản", discount: 50000, shippingFee: 30000, items: [{ name: "Sản phẩm 9", quantity: 3, price: 600000, image: "/images/product9.jpg" }] },
    { id: 9, customerName: "Nguyễn Phương I", total: 1100000, status: "Đang giao", date: "2025-09-22 12:30", deliveryAddress: "222 Lê Văn Sỹ, TP.HCM", paymentMethod: "Thanh toán khi nhận hàng", discount: 0, shippingFee: 40000, items: [{ name: "Sản phẩm 10", quantity: 2, price: 550000, image: "/images/product10.jpg" }] },
    { id: 10, customerName: "Trần Minh K", total: 700000, status: "Đang xử lý", date: "2025-09-21 5:12", deliveryAddress: "333 Nguyễn Văn Cừ, Hà Nội", paymentMethod: "Chuyển khoản", discount: 20000, shippingFee: 30000, items: [{ name: "Sản phẩm 11", quantity: 7, price: 100000, image: "/images/product11.jpg" }] },
    { id: 11, customerName: "Nguyễn Văn L", total: 1300000, status: "Đã giao", date: "2025-09-20 9:10", deliveryAddress: "444 Trần Hưng Đạo, TP.HCM", paymentMethod: "Thanh toán khi nhận hàng", discount: 100000, shippingFee: 50000, items: [{ name: "Sản phẩm 12", quantity: 2, price: 650000, image: "/images/product12.jpg" }] },
    { id: 12, customerName: "Trần Thi L", total: 2700000, status: "Đã giao", date: "2025-09-19 18:20", deliveryAddress: "555 Lý Thường Kiệt, Đà Nẵng", paymentMethod: "Chuyển khoản", discount: 0, shippingFee: 40000, items: [{ name: "Sản phẩm 13", quantity: 3, price: 900000, image: "/images/product13.jpg" }] },
    { id: 13, customerName: "Lê Thị M", total: 900000, status: "Đang xử lý", date: "2025-09-18 14:30", deliveryAddress: "666 Nguyễn Đình Chiểu, Hà Nội", paymentMethod: "Thanh toán khi nhận hàng", discount: 50000, shippingFee: 30000, items: [{ name: "Sản phẩm 14", quantity: 3, price: 300000, image: "/images/product14.jpg" }] },
    { id: 14, customerName: "Phạm Quỳnh N", total: 1600000, status: "Hủy", date: "2025-09-17 22:05", deliveryAddress: "777 Tôn Đức Thắng, TP.HCM", paymentMethod: "Chuyển khoản", discount: 100000, shippingFee: 40000, items: [{ name: "Sản phẩm 15", quantity: 4, price: 400000, image: "/images/product15.jpg" }] },
    { id: 15, customerName: "Đỗ Minh P", total: 2200000, status: "Đã giao", date: "2025-09-16 15:50", deliveryAddress: "888 Hai Bà Trưng, Hà Nội", paymentMethod: "Thanh toán khi nhận hàng", discount: 0, shippingFee: 50000, items: [{ name: "Sản phẩm 16", quantity: 2, price: 1100000, image: "/images/product16.jpg" }] }
];

// Hàm hiển thị chi tiết đơn hàng
function showOrderDetails(id) {
    const order = orders.find(order => order.id === id);
    const modal = document.getElementById('order-detail-modal');
    const orderTitle = document.getElementById('order-title');
    const orderDate = document.getElementById('order-date');
    const customerName = document.getElementById('customer-name-value');
    const orderStatus = document.getElementById('order-status-value');
    const deliveryAddress = document.getElementById('delivery-address-value');
    const paymentMethod = document.getElementById('payment-method-value');
    const subtotal = document.getElementById('subtotal-value');
    const discount = document.getElementById('discount-value');
    const shippingFee = document.getElementById('shipping-fee-value');
    const total = document.getElementById('total-value');
    const productSection = document.getElementById('product-section');

    orderTitle.textContent = `Đơn hàng #${order.id}`;
    orderDate.textContent = new Date(order.date).toLocaleString('vi-VN', { 
        hour: '2-digit', minute: '2-digit', hour12: false,
        year: 'numeric', month: '2-digit', day: '2-digit' 
    });
    customerName.textContent = order.customerName;
    orderStatus.textContent = order.status;
    deliveryAddress.textContent = order.deliveryAddress;
    paymentMethod.textContent = order.paymentMethod;
    subtotal.textContent = (order.total - order.shippingFee + order.discount).toLocaleString('vi-VN') + ' VND';
    discount.textContent = order.discount.toLocaleString('vi-VN') + ' VND';
    shippingFee.textContent = order.shippingFee.toLocaleString('vi-VN') + ' VND';
    total.textContent = order.total.toLocaleString('vi-VN') + ' VND';

    productSection.innerHTML = order.items.map(item => `
        <div class="product-item">
            <img src="${item.image}" class="product-image" alt="${item.name}">
            <div class="product-info">
                <p class="product-name">${item.name}</p>
                <p class="product-quantity">Số lượng: ${item.quantity}</p>
            </div>
            <span class="price">${(item.price * item.quantity).toLocaleString('vi-VN')} VND</span>
        </div>
    `).join('');

    modal.style.display = 'block';
}

// Hàm cập nhật trạng thái
function showUpdateStatusModal(id) {
    const order = orders.find(order => order.id === id);
    const modal = document.getElementById('update-status-modal');
    const content = document.getElementById('update-status-content');

    content.innerHTML = `
        <p><strong>ID Đơn hàng:</strong> ${order.id}</p>
        <p><strong>Khách hàng:</strong> ${order.customerName}</p>
        <select class="status-select">
            <option value="Đang xử lý" ${order.status === 'Đang xử lý' ? 'selected' : ''}>Đang xử lý</option>
            <option value="Đang giao" ${order.status === 'Đang giao' ? 'selected' : ''}>Đang giao</option>
            <option value="Đã giao" ${order.status === 'Đã giao' ? 'selected' : ''}>Đã giao</option>
            <option value="Hủy" ${order.status === 'Hủy' ? 'selected' : ''}>Hủy</option>
        </select>
    `;
    modal.style.display = 'block';

    document.getElementById('confirm-update-status').onclick = () => {
        const newStatus = content.querySelector('.status-select').value;
        order.status = newStatus;
        const filteredOrders = getFilteredAndSortedOrders();
        renderOrders(filteredOrders);
        renderPagination(filteredOrders);
        modal.style.display = 'none';
    };
}

// Hàm lấy danh sách đã lọc và sắp xếp
function getFilteredAndSortedOrders() {
    const searchQuery = document.getElementById('customer-search').value.toLowerCase();
    const sortOrder = document.getElementById('sort-filter').value;

    let filteredOrders = orders.filter(order => order.customerName.toLowerCase().includes(searchQuery));

    if (sortOrder === "latest") {
        filteredOrders.sort((a, b) => new Date(b.date) - new Date(a.date));
    } else {
        filteredOrders.sort((a, b) => new Date(a.date) - new Date(b.date));
    }

    return filteredOrders;
}

// Hiển thị danh sách đơn hàng
function renderOrders(orderList) {
    const tbody = document.querySelector('#order-table tbody');
    tbody.innerHTML = '';

    const ordersToShow = paginateOrders(orderList);

    ordersToShow.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${order.id}</td>
            <td>${order.customerName}</td>
            <td>${order.total.toLocaleString('vi-VN')} VND</td>
            <td>${order.status}</td>
            <td>${new Date(order.date).toLocaleString('vi-VN', { 
                hour: '2-digit', minute: '2-digit', hour12: false,
                year: 'numeric', month: '2-digit', day: '2-digit' 
            })}</td>
            <td class="action-buttons">
                <button class="detail-btn" data-id="${order.id}">Xem chi tiết</button>
                <button class="update-status-btn" data-id="${order.id}">Cập nhật trạng thái</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Thêm sự kiện cho các nút
    document.querySelectorAll('.detail-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            const orderId = parseInt(e.target.dataset.id);
            showOrderDetails(orderId);
        });
    });

    document.querySelectorAll('.update-status-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            const orderId = parseInt(e.target.dataset.id);
            showUpdateStatusModal(orderId);
        });
    });
}

// Phân trang
let currentPage = 1;
const ordersPerPage = 10;

function paginateOrders(orderList) {
    const startIndex = (currentPage - 1) * ordersPerPage;
    const endIndex = startIndex + ordersPerPage;
    return orderList.slice(startIndex, endIndex);
}

function renderPagination(orderList) {
    const totalPages = Math.ceil(orderList.length / ordersPerPage);
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');

    prevPageBtn.disabled = currentPage === 1;
    nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;

    pageInfo.textContent = `Trang ${currentPage} / ${totalPages || 1}`;
}

// Sự kiện áp dụng bộ lọc
document.getElementById('apply-filter').addEventListener('click', () => {
    currentPage = 1;
    const filteredOrders = getFilteredAndSortedOrders();
    renderOrders(filteredOrders);
    renderPagination(filteredOrders);
});

// Sự kiện hiển thị tất cả
document.getElementById('show-all').addEventListener('click', () => {
    currentPage = 1;
    document.getElementById('customer-search').value = '';
    document.getElementById('sort-filter').value = 'latest';
    renderOrders(orders);
    renderPagination(orders);
});

// Sự kiện chuyển trang trước
document.getElementById('prev-page').addEventListener('click', () => {
    if (currentPage > 1) {
        currentPage--;
        const filteredOrders = getFilteredAndSortedOrders();
        renderOrders(filteredOrders);
        renderPagination(filteredOrders);
    }
});

// Sự kiện chuyển trang tiếp theo
document.getElementById('next-page').addEventListener('click', () => {
    const filteredOrders = getFilteredAndSortedOrders();
    const totalPages = Math.ceil(filteredOrders.length / ordersPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        const updatedFilteredOrders = getFilteredAndSortedOrders();
        renderOrders(updatedFilteredOrders);
        renderPagination(updatedFilteredOrders);
    }
});

// Sự kiện đóng modal
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('close-btn') || e.target.classList.contains('modal')) {
        document.getElementById('order-detail-modal').style.display = 'none';
        document.getElementById('update-status-modal').style.display = 'none';
    }
});

// Khi trang tải, hiển thị 10 đơn hàng đầu tiên
document.addEventListener('DOMContentLoaded', function () {
    renderOrders(orders);
    renderPagination(orders);
});