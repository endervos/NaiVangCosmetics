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

async function loadOrders(status) {
    let url = '/api/orders';
    if (status !== 'all') {
        url += `?status=${status}`;
    }

    try {
        const response = await fetch(url);
        if (!response.ok) {
            if (response.status === 401) {
                alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                window.location.href = "/login";
                return;
            }
            if (response.status === 403) {
                alert("Bạn không có quyền truy cập.");
                return;
            }
            throw new Error("Không thể tải đơn hàng");
        }

        const data = await response.json();
        renderOrders(status, data);
    } catch (error) {
        console.error("Lỗi tải đơn hàng:", error);
        alert("Không thể tải đơn hàng. Vui lòng thử lại sau.");
    }
}

function renderOrders(status, orders) {
    const container = document.getElementById(`order-${status}`);
    if (!container) return;

    container.innerHTML = '';

    if (!orders || orders.length === 0) {
        container.innerHTML = '<p>Không có đơn hàng nào.</p>';
        return;
    }

    for (const order of orders) {
        const encryptedId = order.encryptedOrderId;

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
                <button class="btn-detail" onclick="viewOrderDetail('${encryptedId}')">Xem chi tiết</button>
                ${(order.status === 'PENDING' || order.status === 'PAID') ?
                    `<button class="btn-cancel" onclick="cancelOrder('${encryptedId}')">Hủy đơn</button>` :
                    ''}
            </div>
        `;
        container.appendChild(item);
    }
}

function formatCurrency(amount) {
    return amount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN');
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': 'Đang chờ xác nhận',
        'PAID': 'Đã thanh toán',
        'PROCESSING': 'Đang xử lý',
        'SHIPPED': 'Đang giao hàng',
        'COMPLETED': 'Hoàn tất',
        'CANCELLED': 'Đã hủy',
        'REFUNDED': 'Đã hoàn tiền'
    };
    return statusMap[status] || status;
}

function viewOrderDetail(encryptedId) {
    window.location.href = `/orderManage/detail/${encryptedId}`;
}

async function cancelOrder(encryptedId) {
    if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
        return;
    }

    try {
        const response = await fetch(`/api/orders/${encryptedId}/cancel`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const result = await response.json();

        if (result.success) {
            alert('Đã hủy đơn hàng thành công!');
            const activeTab = document.querySelector('.tablinks.active');
            if (activeTab) {
                const status = activeTab.textContent.toLowerCase().includes('tất cả') ? 'all' :
                              activeTab.getAttribute('onclick').match(/'([^']+)'/)[1];
                loadOrders(status);
            }
        } else {
            alert(result.message || 'Không thể hủy đơn hàng');
        }
    } catch (error) {
        console.error('Lỗi khi hủy đơn:', error);
        alert('Đã xảy ra lỗi khi hủy đơn hàng');
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadOrders('all');
    document.querySelector(".tablinks").classList.add("active");
    document.getElementById("all").style.display = "block";
});