const orderData = {
    orderId: "#250909524759",
    orderDate: "09/09/2025, 15:29",
    deliveryAddress: "hiin- 0123456789, 97, Man Thiện, Phường Hiệp Phú, Quận 9, Hồ Chí Minh",
    paymentMethod: "Thanh toán khi nhận hàng",
    subtotal: "464.000 đ",
    discount: "0 đ",
    shippingFee: "0 đ",
    total: "464.000 đ",
    product: [
        {
            id: 1,
            image: "/src/main/resources/static/DetailOrder/Image/Item1.png",
            name: "Cocoon Combo 2 Nước Tẩy Trang Bioderma Cocoon Làm Sạch & Giảm Dầu 500ml (2x500ml)",
            quantity: 1,
            price: "232.000 đ",
            status: "completed" // Sản phẩm đã nhận
        },
        {
            id: 2,
            image: "/src/main/resources/static/DetailOrder/Image/Item2.png",
            name: "Serum Dưỡng Da Trắng Sáng & Cấp Ẩm 50ml",
            quantity: 2,
            price: "232.000 đ",
            status: "completed" // Sản phẩm đã nhận
        }
    ]
};


// ============================================
// CẬP NHẬT THÔNG TIN ĐƠN HÀNG
function updateOrderDetails(orderData) {
    const titleEl = document.getElementById("order-title");
    const dateEl = document.getElementById("order-date");
    const deliveryEl = document.getElementById("delivery-address-value");
    const paymentEl = document.getElementById("payment-method-value");
    const subtotalEl = document.getElementById("subtotal-value");
    const discountEl = document.getElementById("discount-value");
    const shippingEl = document.getElementById("shipping-fee-value");
    const totalEl = document.getElementById("total-value");
    const productSection = document.getElementById("product-section");

    if(titleEl) titleEl.textContent = `Chi tiết đơn hàng ${orderData.orderId}`;
    if(dateEl) dateEl.textContent = `Ngày đặt: ${orderData.orderDate}`;
    if(deliveryEl) deliveryEl.textContent = orderData.deliveryAddress;
    if(paymentEl) paymentEl.textContent = orderData.paymentMethod;
    if(subtotalEl) subtotalEl.textContent = orderData.subtotal;
    if(discountEl) discountEl.textContent = orderData.discount;
    if(shippingEl) shippingEl.textContent = orderData.shippingFee;
    if(totalEl) totalEl.textContent = orderData.total;

    if(productSection) {
        productSection.innerHTML = ""; // Xóa sản phẩm cũ
        orderData.product.forEach(prod => {
            const div = document.createElement("div");
            div.classList.add("product-item");
            div.innerHTML = `
                <img src="${prod.image}" class="product-image" alt="${prod.name}">
                <div class="product-info">
                    <p class="product-name">${prod.name}</p>
                    <p class="product-quantity">Số lượng: ${prod.quantity}</p>
                </div>
                <span class="price">${prod.price}</span>
            `;
            productSection.appendChild(div);
        });
    }
}

// ============================================
// MỞ MODAL ĐÁNH GIÁ
function openReview() {
    const productSection = document.getElementById("product-section");
    if (!productSection) return;

    const productItems = productSection.querySelectorAll(".product-item");
    if (productItems.length === 0) {
        alert("Chưa có sản phẩm nào đủ điều kiện đánh giá!");
        return;
    }

    // Xóa modal cũ nếu có
    const oldModal = document.querySelector(".review-modal");
    if(oldModal) oldModal.remove();

    // Tạo modal
    const modal = document.createElement("div");
    modal.classList.add("review-modal");
    modal.innerHTML = `
        <div class="review-modal-content">
            <span class="close-btn">&times;</span>
            <h2>Đánh giá sản phẩm</h2>
            <div id="review-products-container"></div>
            <button id="submit-review" class="submit-btn">Gửi đánh giá</button>
        </div>
    `;
    document.body.appendChild(modal);

    const container = modal.querySelector("#review-products-container");

    productItems.forEach((prodEl, index) => {
        const img = prodEl.querySelector(".product-image")?.src || "";
        const name = prodEl.querySelector(".product-name")?.textContent || "";
        const quantity = prodEl.querySelector(".product-quantity")?.textContent || "";
        const price = prodEl.querySelector(".price")?.textContent || "";

        const div = document.createElement("div");
        div.classList.add("review-product");
        div.innerHTML = `
            <img src="${img}" alt="${name}">
            <div class="review-product-info">
                <p class="review-product-name">${name}</p>
                <p class="review-product-quantity">${quantity}</p>
                <div class="star-rating" data-product-index="${index}">
                    <span data-value="1">&#9733;</span>
                    <span data-value="2">&#9733;</span>
                    <span data-value="3">&#9733;</span>
                    <span data-value="4">&#9733;</span>
                    <span data-value="5">&#9733;</span>
                </div>
                <textarea class="review-comment" placeholder="Viết nhận xét..."></textarea>
            </div>
        `;
        container.appendChild(div);
    });

    modal.style.display = "block";

    // Đóng modal
    modal.querySelector(".close-btn").onclick = () => modal.remove();
    window.onclick = (event) => { if (event.target === modal) modal.remove(); }

    // Star rating
    modal.querySelectorAll(".star-rating").forEach(ratingDiv => {
        const stars = ratingDiv.querySelectorAll("span");
        stars.forEach(star => {
            star.addEventListener("click", () => {
                stars.forEach(s => s.classList.remove("active"));
                for(let i=0;i<star.dataset.value;i++){
                    stars[i].classList.add("active");
                }
                ratingDiv.dataset.rating = star.dataset.value;
            });
        });
    });

    // Gửi đánh giá
    modal.querySelector("#submit-review").onclick = () => {
        const reviews = [];
        modal.querySelectorAll(".review-product").forEach((prodDiv, index) => {
            const rating = prodDiv.querySelector(".star-rating")?.dataset.rating || 0;
            const comment = prodDiv.querySelector(".review-comment")?.value || "";
            reviews.push({index, rating, comment});
        });

        if(!reviews.some(r => r.rating > 0)) {
            alert("Vui lòng đánh giá ít nhất 1 sản phẩm!");
            return;
        }

        console.log("Đánh giá gửi đi:", reviews);
        alert("Cảm ơn bạn đã gửi đánh giá!");
        modal.remove();
    }
}

// ============================================
// QUAY LẠI TRANG TRƯỚC
function goBack() {
    window.history.back();
}

// ============================================
// LOAD DỮ LIỆU KHI DOM SẴN SÀNG
document.addEventListener("DOMContentLoaded", () => {
    updateOrderDetails(orderData);
});
