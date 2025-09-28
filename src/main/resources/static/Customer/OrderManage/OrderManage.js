// Function to load tab content based on selected status
function loadTab(evt, status) {
    let i, tabcontent, tablinks;

    // Hide all tab contents
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Remove active class from all tab links
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].classList.remove("active");
    }

    // Show the current tab and add active class to the button
    document.getElementById(status).style.display = "block";
    evt.currentTarget.classList.add("active");

    // Load orders dynamically based on status
    loadOrders(status);
}

// Function to load orders for a specific status
function loadOrders(status) {
    let orders = {
        all: [
            { id: "001", name: "T-Shirt", qty: 1, image: "hinh1.jpg", total: "232.000", status: "pending", deliveryDate: "09/09/2025 18:00", items: [
                {name: "T-Shirt", qty: 1, image: "hinh1.jpg", price: "232.000"}
            ]},
            { id: "002", name: "Laptop", qty: 1, image: "hinh1.jpg", total: "15.000.000", status: "completed", deliveryDate: "10/09/2025 12:00", items: [
                {name: "Laptop", qty: 1, image: "hinh1.jpg", price: "15.000.000"}
            ]},
            { id: "003", name: "Set đồ", qty: 3, image: "hinh1.jpg", total: "500.000", status: "pending", deliveryDate: "11/09/2025 10:00", items: [
                {name: "Mũ", qty: 1, image: "hinh1.jpg", price: "100.000"},
                {name: "Áo", qty: 1, image: "hinh1.jpg", price: "150.000"},
                {name: "Giày", qty: 1, image: "hinh1.jpg", price: "250.000"}
            ]}
        ],
        new: [
            { id: "003", name: "Set đồ", qty: 3, image: "hinh1.jpg", total: "500.000", status: "pending", deliveryDate: "11/09/2025 10:00", items: [
                {name: "Mũ", qty: 1, image: "hinh1.jpg", price: "100.000"},
                {name: "Áo", qty: 1, image: "hinh1.jpg", price: "150.000"},
                {name: "Giày", qty: 1, image: "hinh1.jpg", price: "250.000"}
            ]}
        ],
        processing: [],
        shipping: [],
        completed: [
            { id: "002", name: "Laptop", qty: 1, image: "hinh1.jpg", total: "15.000.000", status: "completed", deliveryDate: "10/09/2025 12:00", items: [
                {name: "Laptop", qty: 1, image: "hinh1.jpg", price: "15.000.000"}
            ]}
        ],
        cancelled: []
    };

    const orderContainer = document.getElementById(`order-${status}`);
    orderContainer.innerHTML = '';

    orders[status].forEach(order => {
        const orderElement = document.createElement('div');
        orderElement.classList.add('order-item');

        let itemDetailsHTML = '';
        order.items.forEach(item => {
            itemDetailsHTML += `
                <div class="item-details">
                    <img src="${item.image}" alt="${item.name}">
                    <div class="item-info">
                        <span>${item.name}</span>
                        <span class="item-info-qty"><pre>${item.qty}  X  ${item.price} ₫</pre></span>
                        </span>
                    </div>
                </div>
            `;
        });

        orderElement.innerHTML = `
            <div class="order-item-header">
            <span class="order-item-header-orderid">#${order.id}</span>
            
             <span class="order-item-header-button"><a href="details.html?id=${order.id}" class="detail-link">Chi tiết</a>
</span>


            </div>
            <div class="order-item-body">
                <div class="info">
                <span class="info-item">Kiện: ${order.items.length}/${order.items.length} |
                Giao: ${order.deliveryDate} |
                Tình trạng: <span class="status ${order.status}">${order.status === 'pending' ? 'Chờ Xử Lý' : 'Hoàn Tất'}</span></span>
                <span class="info-item-total">Tổng tiền (${order.items.length} sản phẩm): ${order.total} ₫</span>
                </div>
                ${itemDetailsHTML}
            </div>
            <div class="order-item-footer">
                <div class="order-item-footer-body">
                   
                    <button class="reorder-btn">Mua Lại</button>
                </div>
            </div>
        `;

        orderContainer.appendChild(orderElement);
    });
}

// Default to showing "Tất Cả" tab on page load
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.tablinks').click(); // Activate the first tab
});
