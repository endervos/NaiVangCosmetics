document.addEventListener("DOMContentLoaded", () => {
    const addBtn = document.getElementById("addAddressBtn");
    const container = document.getElementById("addressContainer");

    addBtn.addEventListener("click", function () {
        const newInput = document.createElement("input");
        newInput.type = "text";
        newInput.name = "address";
        newInput.placeholder = "Nhập địa chỉ...";
        newInput.classList.add("dynamic-input"); // để style riêng nếu muốn
        container.appendChild(newInput);
    });
});
