document.addEventListener("DOMContentLoaded", function () {
    const addBtn = document.getElementById("addAddressBtn");
    const container = document.getElementById("addressContainer");

    addBtn.addEventListener("click", function () {
        const newInput = document.createElement("input");
        newInput.type = "text";
        newInput.className = "address-input";
        newInput.placeholder = "Nhập địa chỉ";
        container.appendChild(newInput);
    });
});