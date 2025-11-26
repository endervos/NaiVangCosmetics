function loadTab(event, status) {
    const tabcontents = document.getElementsByClassName("tabcontent");
    for (let content of tabcontents) {
        content.style.display = "none";
    }

    const tablinks = document.getElementsByClassName("tablinks");
    for (let link of tablinks) {
        link.classList.remove("active");
    }

    document.getElementById(status).style.display = "block";
    event.currentTarget.classList.add("active");

    loadOrders(status);
}

function loadOrders(status) {
    if (typeof customerId === 'undefined' || customerId === 0) {
        console.error("customerId chưa được gán!");
        return;
    }

    let url = `/api/orders/${customerId}`;
    if (status !== 'all') {
        url += `?status=${status}`;
    }

    fetch(url)
        .then(res => {
            if (!res.ok) throw new Error("Không thể tải đơn hàng");
            return res.json();
        })
        .then(data => renderOrders(status, data))
        .catch(err => console.error("Lỗi tải đơn hàng:", err));
}

function renderOrders(status, orders) {
    const container = document.getElementById(`order-${status}`);
    if (!container) return;

    container.innerHTML = '';

    if (!orders || orders.length === 0) {
        container.innerHTML = '<p>Không có đơn hàng nào.</p>';
        return;
    }

    orders.forEach(order => {
        const item = document.createElement('div');
        item.classList.add('order-card');
        item.innerHTML = `
            <div class="order-header">
                <div>
                    <p><strong>Mã đơn hàng:</strong> ${order.orderId}</p>
                    <p><strong>Trạng thái:</strong> ${getStatusText(order.status)}</p>
                </div>
                <div class="order-date">
                    <p><strong>Ngày đặt:</strong> ${formatDate(order.placedAt)}</p>
                </div>
            </div>
            <div class="order-body">
                <p><strong>Tổng tiền:</strong> ${formatCurrency(order.total)}</p>
            </div>
             <div class="order-footer">
                <button class="btn-detail" onclick="viewOrderDetail(${order.orderId})">Xem chi tiết</button>
             </div>
        `;
        container.appendChild(item);
    });
}

function formatCurrency(amount) {
    return amount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN');
}

function getStatusText(status) {
    switch (status) {
        case 'PENDING':
            return 'Đang chờ xác nhận';
        case 'PAID':
            return 'Đã thanh toán';
        case 'PROCESSING':
            return 'Đang xử lý';
        case 'SHIPPED':
            return 'Đang giao hàng';
        case 'COMPLETED':
            return 'Hoàn tất';
        case 'CANCELLED':
            return 'Đã hủy';
        default:
            return status;
    }
}

function viewOrderDetail(orderId) {
    window.location.href = `/orderManage/detail/${orderId}`;
}

// Khi trang load xong, hiển thị tab “Tất cả”
document.addEventListener("DOMContentLoaded", () => {
    loadOrders('all');
    document.querySelector(".tablinks").classList.add("active");
    document.getElementById("all").style.display = "block";
});
