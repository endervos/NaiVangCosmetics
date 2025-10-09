document.addEventListener("DOMContentLoaded", () => {
    const quantityInputs = document.querySelectorAll(".quantity");
    const summarySubtotal = document.getElementById("summary-subtotal");
    const summaryTotal = document.getElementById("summary-total");

    function parsePrice(priceText) {
        // "59.000₫" -> 59000
        return parseInt(priceText.replace(/[^\d]/g, ""));
    }

    function updateCart() {
        let subtotal = 0;

        document.querySelectorAll("#cart-body tr").forEach((row) => {
            let priceText = row.querySelector(".price-sale").textContent;
            let price = parsePrice(priceText);
            let qty = parseInt(row.querySelector(".quantity").value) || 0;
            let rowTotal = price * qty;

            subtotal += rowTotal;
            row.querySelector(".subtotal").textContent =
                rowTotal.toLocaleString("vi-VN") + "₫";
        });

        if (summarySubtotal && summaryTotal) {
            summarySubtotal.textContent = subtotal.toLocaleString("vi-VN") + "₫";
            summaryTotal.textContent = subtotal.toLocaleString("vi-VN") + "₫";
        }
    }

    quantityInputs.forEach((input) => {
        input.addEventListener("change", updateCart);
    });

    document.querySelectorAll(".remove-btn").forEach((btn) => {
        btn.addEventListener("click", (e) => {
            e.target.closest("tr").remove();
            updateCart();
        });
    });

    updateCart();
});
