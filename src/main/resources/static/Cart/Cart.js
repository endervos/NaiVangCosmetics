document.addEventListener("DOMContentLoaded", () => {
    const quantityInputs = document.querySelectorAll(".quantity");
    const subtotalCells = document.querySelectorAll(".subtotal");
    const summarySubtotal = document.getElementById("summary-subtotal");
    const summaryTotal = document.getElementById("summary-total");

    function updateCart() {
        let subtotal = 0;

        document.querySelectorAll("#cart-body tr").forEach((row, index) => {
            let price = 59000; // ví dụ giá sau giảm
            let qty = parseInt(row.querySelector(".quantity").value);
            let rowTotal = price * qty;
            subtotal += rowTotal;
            row.querySelector(".subtotal").textContent =
                rowTotal.toLocaleString() + "₫";
        });

        summarySubtotal.textContent = subtotal.toLocaleString() + "₫";
        summaryTotal.textContent = subtotal.toLocaleString() + "₫";
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

    document.getElementById("checkout-btn").addEventListener("click", () => {
        alert("Đặt hàng thành công! Cảm ơn bạn.");
    });

    updateCart();
});
