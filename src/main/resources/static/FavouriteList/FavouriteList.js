document.addEventListener("DOMContentLoaded", () => {
    // Lấy tất cả nút ❌ trong danh sách yêu thích
    const removeButtons = document.querySelectorAll(
        ".favorite-content .remove-btn"
    );

    removeButtons.forEach((btn) => {
        btn.addEventListener("click", (e) => {
            const product = e.target.closest(".product-card"); // tìm thẻ cha
            if (product) {
                product.remove(); // xóa sản phẩm
            }
        });
    });
});
