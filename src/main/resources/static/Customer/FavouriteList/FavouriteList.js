document.addEventListener("DOMContentLoaded", () => {
    const removeButtons = document.querySelectorAll(
        ".favorite-content .remove-btn"
    );

    removeButtons.forEach((btn) => {
        btn.addEventListener("click", (e) => {
            const product = e.target.closest(".product-card");
            if (product) {
                product.remove();
            }
        });
    });
});
