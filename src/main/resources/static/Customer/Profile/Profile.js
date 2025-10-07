document.addEventListener("DOMContentLoaded", function () {
    const addAddressBtn = document.getElementById("addAddressBtn");
    const addressContainer = document.getElementById("addressContainer");

    if (!addAddressBtn || !addressContainer) {
        console.error("Không tìm thấy #addAddressBtn hoặc #addressContainer trong DOM!");
        return;
    }

    // Cập nhật lại số thứ tự hiển thị của địa chỉ
    function reindexAddresses() {
        const rows = addressContainer.querySelectorAll(".address-row");
        rows.forEach((row, i) => {
            const label = row.querySelector(".address-label");
            if (label) label.textContent = `Địa chỉ ${i + 1}`;
        });
    }

    // Tạo một dòng địa chỉ mới
    function createAddressRow(index) {
        const newRow = document.createElement("div");
        newRow.classList.add("address-row");
        newRow.innerHTML = `
            <span class="address-label">Địa chỉ ${index}</span>
            <input type="text" name="addresses[]" placeholder="Nhập địa chỉ..." class="address-input" />
            <button type="button" class="removeAddressBtn btn-remove">–</button>
        `;

        // Thêm sự kiện xóa cho nút “–”
        const removeBtn = newRow.querySelector(".removeAddressBtn");
        removeBtn.addEventListener("click", () => {
            newRow.remove();
            reindexAddresses();
        });

        return newRow;
    }

    // Sự kiện khi nhấn nút "+"
    addAddressBtn.addEventListener("click", () => {
        const inputs = addressContainer.querySelectorAll(".address-input");
        const lastInput = inputs[inputs.length - 1];

        // Ngăn thêm mới nếu chưa nhập địa chỉ trước đó
        if (lastInput && lastInput.value.trim() === "") {
            alert("Vui lòng nhập địa chỉ trước khi thêm mới!");
            return;
        }

        const newIndex = inputs.length + 1;
        const newRow = createAddressRow(newIndex);
        addressContainer.appendChild(newRow);
    });

    // Gán sự kiện xóa cho các dòng có sẵn (nếu load từ DB)
    addressContainer.querySelectorAll(".removeAddressBtn").forEach(btn => {
        btn.addEventListener("click", (e) => {
            e.target.closest(".address-row").remove();
            reindexAddresses();
        });
    });
});
