const orders = [
  {
    id: "DH001",
    customer: "Nguyễn Văn A",
    total: 1500000,
    status: "Đang xử lý",
    date: "2025-10-07",
    address: "123 Đường Láng, Hà Nội",
    paymentMethod: "Thanh toán khi nhận hàng",
    subtotal: 1400000,
    discount: 0,
    shippingFee: 100000,
    products: [
      { name: "Sản phẩm A", quantity: 2, price: 500000, image: "/path/to/product-a.jpg" },
      { name: "Sản phẩm B", quantity: 1, price: 400000, image: "/path/to/product-b.jpg" }
    ]
  },
  {
    id: "DH002",
    customer: "Trần Thị B",
    total: 2000000,
    status: "Đã giao",
    date: "2025-10-06",
    address: "456 Nguyễn Trãi, TP.HCM",
    paymentMethod: "Thẻ tín dụng",
    subtotal: 1900000,
    discount: 100000,
    shippingFee: 100000,
    products: [
      { name: "Sản phẩm C", quantity: 1, price: 1900000, image: "/path/to/product-c.jpg" }
    ]
  }
];

// Pagination variables
let currentPage = 1;
const ordersPerPage = 5;

// DOM elements
const tbody = document.querySelector("#order-table tbody");
const customerSearch = document.querySelector("#customer-search");
const sortFilter = document.querySelector("#sort-filter");
const applyFilterBtn = document.querySelector("#apply-filter");
const showAllBtn = document.querySelector("#show-all");
const prevPageBtn = document.querySelector("#prev-page");
const nextPageBtn = document.querySelector("#next-page");
const pageInfo = document.querySelector("#page-info");
const orderDetailModal = document.querySelector("#order-detail-modal");
const updateStatusModal = document.querySelector("#update-status-modal");
const closeButtons = document.querySelectorAll(".close-btn");
const confirmUpdateStatusBtn = document.querySelector("#confirm-update-status");

// Format currency (VND)
function formatCurrency(amount) {
  return amount.toLocaleString("vi-VN", { style: "currency", currency: "VND" });
}

// Display orders in table
function displayOrders(ordersToDisplay) {
  tbody.innerHTML = "";
  const start = (currentPage - 1) * ordersPerPage;
  const end = start + ordersPerPage;
  const paginatedOrders = ordersToDisplay.slice(start, end);

  paginatedOrders.forEach(order => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${order.id}</td>
      <td>${order.customer}</td>
      <td>${formatCurrency(order.total)}</td>
      <td>${order.status}</td>
      <td>${order.date}</td>
      <td class="action-buttons">
        <button class="detail-btn" onclick="viewOrderDetails('${order.id}')">Xem</button>
        <button class="update-status-btn" onclick="updateOrderStatus('${order.id}')">Cập nhật</button>
      </td>
    `;
    tbody.appendChild(row);
  });

  // Update pagination info
  pageInfo.textContent = `Trang ${currentPage} / ${Math.ceil(ordersToDisplay.length / ordersPerPage)}`;
  prevPageBtn.disabled = currentPage === 1;
  nextPageBtn.disabled = end >= ordersToDisplay.length;
}

// Filter and sort orders
function filterAndSortOrders() {
  let filteredOrders = [...orders];
  
  // Filter by customer name
  const searchTerm = customerSearch.value.trim().toLowerCase();
  if (searchTerm) {
    filteredOrders = filteredOrders.filter(order => 
      order.customer.toLowerCase().includes(searchTerm)
    );
  }

  // Sort by date
  if (sortFilter.value === "latest") {
    filteredOrders.sort((a, b) => new Date(b.date) - new Date(a.date));
  } else if (sortFilter.value === "oldest") {
    filteredOrders.sort((a, b) => new Date(a.date) - new Date(b.date));
  }

  currentPage = 1; // Reset to first page
  displayOrders(filteredOrders);
}

// View order details in modal
function viewOrderDetails(orderId) {
  const order = orders.find(o => o.id === orderId);
  if (!order) return;

  // Populate modal
  document.querySelector("#order-title").textContent = `Đơn hàng #${order.id}`;
  document.querySelector("#order-date").textContent = `Ngày đặt: ${order.date}`;
  document.querySelector("#customer-name-value").textContent = order.customer;
  document.querySelector("#order-status-value").textContent = order.status;
  document.querySelector("#delivery-address-value").textContent = order.address;
  document.querySelector("#payment-method-value").textContent = order.paymentMethod;
  document.querySelector("#subtotal-value").textContent = formatCurrency(order.subtotal);
  document.querySelector("#discount-value").textContent = formatCurrency(order.discount);
  document.querySelector("#shipping-fee-value").textContent = formatCurrency(order.shippingFee);
  document.querySelector("#total-value").textContent = formatCurrency(order.total);

  // Populate products
  const productSection = document.querySelector("#product-section");
  productSection.innerHTML = "<h3>Sản phẩm</h3>";
  order.products.forEach(product => {
    const productDiv = document.createElement("div");
    productDiv.className = "product-item";
    productDiv.innerHTML = `
      <img src="${product.image}" alt="${product.name}" class="product-image">
      <div class="product-info">
        <h4 class="product-name">${product.name}</h4>
        <p class="product-quantity">Số lượng: ${product.quantity}</p>
        <p class="price">Giá: ${formatCurrency(product.price)}</p>
        <p class="price">Tổng: ${formatCurrency(product.price * product.quantity)}</p>
      </div>
    `;
    productSection.appendChild(productDiv);
  });

  orderDetailModal.style.display = "flex";
}

// Update order status modal
function updateOrderStatus(orderId) {
  const order = orders.find(o => o.id === orderId);
  if (!order) return;

  const updateStatusContent = document.querySelector("#update-status-content");
  updateStatusContent.innerHTML = `
    <p>Đơn hàng #${order.id}</p>
    <label for="new-status">Trạng thái mới:</label>
    <select id="new-status" class="status-select">
      <option value="Đang xử lý" ${order.status === "Đang xử lý" ? "selected" : ""}>Đang xử lý</option>
      <option value="Đã giao" ${order.status === "Đã giao" ? "selected" : ""}>Đã giao</option>
      <option value="Đã hủy" ${order.status === "Đã hủy" ? "selected" : ""}>Đã hủy</option>
    </select>
  `;

  updateStatusModal.style.display = "flex";

  // Handle confirm update
  confirmUpdateStatusBtn.onclick = () => {
    const newStatus = document.querySelector("#new-status").value;
    order.status = newStatus;
    updateStatusModal.style.display = "none";
    filterAndSortOrders();
  };
}

// Event listeners
applyFilterBtn.addEventListener("click", filterAndSortOrders);
showAllBtn.addEventListener("click", () => {
  customerSearch.value = "";
  sortFilter.value = "latest";
  currentPage = 1;
  displayOrders(orders);
});
prevPageBtn.addEventListener("click", () => {
  if (currentPage > 1) {
    currentPage--;
    filterAndSortOrders();
  }
});
nextPageBtn.addEventListener("click", () => {
  const maxPage = Math.ceil(orders.length / ordersPerPage);
  if (currentPage < maxPage) {
    currentPage++;
    filterAndSortOrders();
  }
});
closeButtons.forEach(btn => {
  btn.addEventListener("click", () => {
    btn.closest(".modal").style.display = "none";
  });
});

// Close modals when clicking outside
window.addEventListener("click", (event) => {
  if (event.target.classList.contains("modal")) {
    event.target.style.display = "none";
  }
});

// Initialize
document.addEventListener("DOMContentLoaded", () => {
  orderDetailModal.style.display = "none";
  updateStatusModal.style.display = "none";
  displayOrders(orders);
});