function openReview() {
    const modal = document.getElementById("review-modal");
    if (!modal) return;

    modal.style.display = "block";

    const container = modal.querySelector("#review-products-container");
    container.innerHTML = ""; // Xóa nội dung cũ

    document.querySelectorAll(".product-item").forEach((prodEl, index) => {
        const img = prodEl.querySelector(".product-image")?.src || "";
        const name = prodEl.querySelector(".product-name")?.textContent || "";
        const quantity = prodEl.querySelector(".product-quantity")?.textContent || "";

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

    // Đóng modal
    modal.querySelector(".close-btn").onclick = () => modal.style.display = "none";
    window.onclick = (e) => { if (e.target === modal) modal.style.display = "none"; }

    // Chọn sao
    modal.querySelectorAll(".star-rating span").forEach(star => {
        star.addEventListener("click", () => {
            const parent = star.parentElement;
            const value = parseInt(star.dataset.value);
            parent.querySelectorAll("span").forEach((s, i) => s.classList.toggle("active", i < value));
            parent.dataset.rating = value;
        });
    });

    // Gửi đánh giá
    modal.querySelector("#submit-review").onclick = () => {
        const reviews = [];
        modal.querySelectorAll(".review-product").forEach((prodDiv, index) => {
            const rating = parseInt(prodDiv.querySelector(".star-rating")?.dataset.rating) || 0;
            const comment = prodDiv.querySelector(".review-comment")?.value || "";
            reviews.push({ index, rating, comment });
        });

        console.log("Đánh giá:", reviews);
        alert("Cảm ơn bạn đã gửi đánh giá!");
        modal.style.display = "none";
    };
}

function goBack() {
    window.history.back();
}
