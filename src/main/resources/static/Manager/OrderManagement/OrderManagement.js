let allOrders = [];
let currentPage = 1;
const ordersPerPage = 10;

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

function formatCurrency(amount) {
  const amountInVnd = typeof amount === 'number' ? amount : parseInt(amount) || 0;
  return amountInVnd.toLocaleString("vi-VN") + " ₫";
}

function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
}

function getStatusText(status) {
  const statusMap = {
    'PENDING': 'Chờ xử lý',
    'PAID': 'Đã thanh toán',
    'PROCESSING': 'Đang xử lý',
    'SHIPPED': 'Đã giao',
    'COMPLETED': 'Hoàn thành',
    'CANCELLED': 'Đã hủy',
    'REFUNDED': 'Hoàn tiền'
  };
  return statusMap[status] || status;
}

async function loadOrders() {
  console.log('🔄 Đang tải danh sách đơn hàng...');
  try {
    const response = await fetch('/manager/api/orders');
    console.log('📡 Response status:', response.status);

    if (!response.ok) {
      const errorText = await response.text();
      console.error('❌ Response error:', errorText);
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('✅ Đã tải được dữ liệu:', data);
    console.log('📊 Số lượng đơn hàng:', data.length);

    allOrders = data.sort((a, b) => new Date(b.placedAt) - new Date(a.placedAt));
    currentPage = 1;
    displayOrders(allOrders);
  } catch (error) {
    console.error('❌ Lỗi khi tải đơn hàng:', error);
    console.error('❌ Error stack:', error.stack);
    alert('Không thể tải danh sách đơn hàng. Vui lòng kiểm tra console và thử lại!\nLỗi: ' + error.message);
  }
}

function displayOrders(ordersToDisplay) {
  console.log('📋 Hiển thị đơn hàng, số lượng:', ordersToDisplay.length);
  tbody.innerHTML = "";

  if (ordersToDisplay.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">Không có đơn hàng nào</td></tr>';
    pageInfo.textContent = "Trang 0 / 0";
    prevPageBtn.disabled = true;
    nextPageBtn.disabled = true;
    return;
  }

  const start = (currentPage - 1) * ordersPerPage;
  const end = start + ordersPerPage;
  const paginatedOrders = ordersToDisplay.slice(start, end);

  paginatedOrders.forEach(order => {
    const row = document.createElement("tr");
    const customerName = order.customerName || "N/A";

    row.innerHTML = `
      <td>${order.orderId}</td>
      <td>${customerName}</td>
      <td>${formatCurrency(order.total)}</td>
      <td>${getStatusText(order.status)}</td>
      <td>${formatDate(order.placedAt)}</td>
      <td class="action-buttons">
        <button class="detail-btn" onclick="viewOrderDetails(${order.orderId})">Xem</button>
        <button class="update-status-btn" onclick="updateOrderStatus(${order.orderId})">Cập nhật</button>
      </td>
    `;
    tbody.appendChild(row);
  });

  const totalPages = Math.ceil(ordersToDisplay.length / ordersPerPage);
  pageInfo.textContent = `Trang ${currentPage} / ${totalPages}`;
  prevPageBtn.disabled = currentPage === 1;
  nextPageBtn.disabled = currentPage >= totalPages;
}

function getFilteredOrders() {
  let filtered = [...allOrders];

  const searchTerm = customerSearch.value.trim().toLowerCase();
  if (searchTerm) {
    filtered = filtered.filter(order => {
      const customerName = order.customerName || "";
      return customerName.toLowerCase().includes(searchTerm);
    });
  }

  if (sortFilter.value === "latest") {
    filtered.sort((a, b) => new Date(b.placedAt) - new Date(a.placedAt));
  } else if (sortFilter.value === "oldest") {
    filtered.sort((a, b) => new Date(a.placedAt) - new Date(b.placedAt));
  }

  return filtered;
}

function filterAndSortOrders() {
  const filteredOrders = getFilteredOrders();
  currentPage = 1;
  displayOrders(filteredOrders);
}

async function viewOrderDetails(orderId) {
  console.log('🔍 Xem chi tiết đơn hàng #' + orderId);
  try {
    const response = await fetch(`/manager/api/orders/${orderId}`);
    console.log('📡 Response status:', response.status);

    if (!response.ok) {
      throw new Error('Không thể tải chi tiết đơn hàng');
    }

    const data = await response.json();
    console.log('✅ Dữ liệu chi tiết:', data);

    document.querySelector("#order-title").textContent = `Đơn hàng #${data.order.orderId}`;
    document.querySelector("#order-date").textContent = `Ngày đặt: ${formatDate(data.order.placedAt)}`;
    document.querySelector("#customer-name-value").textContent = data.customerName || "N/A";
    document.querySelector("#order-status-value").textContent = getStatusText(data.order.status);

    const address = data.address
      ? `${data.address.street}, ${data.address.district}, ${data.address.city} - SĐT: ${data.address.phoneNumber}`
      : "Chưa có địa chỉ";
    document.querySelector("#delivery-address-value").textContent = address;

    const paymentMethod = data.payment?.paymentMethod || "Chưa có thông tin";
    document.querySelector("#payment-method-value").textContent = paymentMethod;

    // ✅ Chỉ hiển thị giảm giá và tổng tiền
    document.querySelector("#discount-value").textContent = formatCurrency(data.giamGia || 0);
    document.querySelector("#total-value").textContent = formatCurrency(data.order.total);

    // ✅ Ẩn tạm tính và phí vận chuyển
    const subtotalElem = document.querySelector("#subtotal");
    const shippingElem = document.querySelector("#shipping-fee");
    if (subtotalElem) subtotalElem.style.display = "none";
    if (shippingElem) shippingElem.style.display = "none";

    const productSection = document.querySelector("#product-section");
    productSection.innerHTML = "<h3>Sản phẩm</h3>";

    if (data.orderItems && data.orderItems.length > 0) {
      data.orderItems.forEach(orderItem => {
        const product = orderItem.item;
        const productDiv = document.createElement("div");
        productDiv.className = "product-item";

        const imageUrl = product?.imageUrl || "/Customer/Example/Image/default-product.jpg";

        productDiv.innerHTML = `
          <img src="${imageUrl}" alt="${product?.name || 'Sản phẩm'}" class="product-image"
               onerror="this.src='/Customer/Example/Image/default-product.jpg'">
          <div class="product-info">
            <h4 class="product-name">${product?.name || 'N/A'}</h4>
            <p class="product-quantity">Số lượng: ${orderItem.quantity}</p>
            <p class="price">Giá: ${formatCurrency(orderItem.preDiscountPrice)}</p>
            <p class="price">Tổng: ${formatCurrency(orderItem.totalPriceCents)}</p>
          </div>
        `;
        productSection.appendChild(productDiv);
      });
    } else {
      productSection.innerHTML += "<p>Không có sản phẩm nào</p>";
    }

    orderDetailModal.style.display = "flex";
  } catch (error) {
    console.error('❌ Lỗi khi tải chi tiết đơn hàng:', error);
    alert('Không thể tải chi tiết đơn hàng. Vui lòng thử lại!\nLỗi: ' + error.message);
  }
}

function updateOrderStatus(orderId) {
  console.log('🔄 Cập nhật trạng thái đơn hàng #' + orderId);
  const order = allOrders.find(o => o.orderId === orderId);
  if (!order) {
    console.error('❌ Không tìm thấy đơn hàng #' + orderId);
    return;
  }

  const updateStatusContent = document.querySelector("#update-status-content");
  updateStatusContent.innerHTML = `
    <p>Đơn hàng #${order.orderId}</p>
    <label for="new-status">Trạng thái mới:</label>
    <select id="new-status" class="status-select">
      <option value="PENDING" ${order.status === "PENDING" ? "selected" : ""}>Chờ xử lý</option>
      <option value="PAID" ${order.status === "PAID" ? "selected" : ""}>Đã thanh toán</option>
      <option value="PROCESSING" ${order.status === "PROCESSING" ? "selected" : ""}>Đang xử lý</option>
      <option value="SHIPPED" ${order.status === "SHIPPED" ? "selected" : ""}>Đã giao</option>
      <option value="COMPLETED" ${order.status === "COMPLETED" ? "selected" : ""}>Hoàn thành</option>
      <option value="CANCELLED" ${order.status === "CANCELLED" ? "selected" : ""}>Đã hủy</option>
      <option value="REFUNDED" ${order.status === "REFUNDED" ? "selected" : ""}>Hoàn tiền</option>
    </select>
  `;

  updateStatusModal.style.display = "flex";

  confirmUpdateStatusBtn.onclick = async () => {
    const newStatus = document.querySelector("#new-status").value;
    console.log('💾 Đang lưu trạng thái mới:', newStatus);

    try {
      const response = await fetch(`/manager/api/orders/${orderId}/status?status=${newStatus}`, {
        method: 'PUT'
      });

      if (!response.ok) {
        throw new Error('Không thể cập nhật trạng thái');
      }

      const result = await response.json();
      console.log('✅ Kết quả cập nhật:', result);

      if (result.success) {
        alert('Cập nhật trạng thái thành công!');
        updateStatusModal.style.display = "none";
        await loadOrders();
      } else {
        alert(result.message || 'Có lỗi xảy ra khi cập nhật');
      }
    } catch (error) {
      console.error('❌ Lỗi khi cập nhật trạng thái:', error);
      alert('Không thể cập nhật trạng thái. Vui lòng thử lại!\nLỗi: ' + error.message);
    }
  };
}

applyFilterBtn.addEventListener("click", filterAndSortOrders);

showAllBtn.addEventListener("click", () => {
  customerSearch.value = "";
  sortFilter.value = "latest";
  currentPage = 1;
  const sortedOrders = [...allOrders].sort((a, b) => new Date(b.placedAt) - new Date(a.placedAt));
  displayOrders(sortedOrders);
});

prevPageBtn.addEventListener("click", () => {
  if (currentPage > 1) {
    currentPage--;
    const filteredOrders = getFilteredOrders();
    displayOrders(filteredOrders);
  }
});

nextPageBtn.addEventListener("click", () => {
  const filteredOrders = getFilteredOrders();
  const maxPage = Math.ceil(filteredOrders.length / ordersPerPage);
  if (currentPage < maxPage) {
    currentPage++;
    displayOrders(filteredOrders);
  }
});

closeButtons.forEach(btn => {
  btn.addEventListener("click", () => {
    btn.closest(".modal").style.display = "none";
  });
});

window.addEventListener("click", (event) => {
  if (event.target.classList.contains("modal")) {
    event.target.style.display = "none";
  }
});

document.addEventListener("DOMContentLoaded", () => {
  console.log('🚀 Trang đã load xong, bắt đầu tải đơn hàng...');
  orderDetailModal.style.display = "none";
  updateStatusModal.style.display = "none";
  loadOrders();
});