document.addEventListener("DOMContentLoaded", function () {
    const container = document.querySelector(".account-info");
    if (!container) return;

    const updateBtn = container.querySelector(".btn");
    const fullnameInput = container.querySelector("#fullname");
    const phoneInput = container.querySelector("#phoneNumber");
    const emailInput = container.querySelector("#email");
    const birthdayInput = container.querySelector("#birthday");
    const genderInputs = container.querySelectorAll('input[name="gender"]');
    if (birthdayInput) {
        flatpickr(birthdayInput, {
            dateFormat: "Y-m-d",
            altInput: true,
            altFormat: "d/m/Y",
            locale: "vn",
            maxDate: "today",
            defaultDate: birthdayInput.value
        });
    }

    const namePattern = /^[\p{L}\s]{2,50}$/u;
    const phonePattern = /^(0[3|5|7|8|9])[0-9]{8}$/;
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const addressPattern = /^[A-Za-zÀ-ỹà-ỹĐđ0-9\s,./()-]{5,100}$/;

    function showError(element, message) {
        if (!element) return;
        let err = element.nextElementSibling;
        if (!err || !err.classList.contains("error")) {
            err = document.createElement("div");
            err.classList.add("error");
            err.style.color = "red";
            err.style.fontSize = "14px";
            err.style.marginTop = "4px";
            element.insertAdjacentElement("afterend", err);
        }
        err.textContent = message;
    }

    function clearErrors() {
        container.querySelectorAll(".error").forEach(e => (e.textContent = ""));
    }

    function validateFullname() {
        const fullname = fullnameInput.value.trim();
        if (fullname === "") {
            showError(fullnameInput, "Họ và tên không được để trống");
            return false;
        } else if (!namePattern.test(fullname)) {
            showError(fullnameInput, "Vui lòng nhập đúng định dạng (chỉ chữ cái, không số hoặc ký tự đặc biệt)");
            return false;
        } else {
            showError(fullnameInput, "");
            return true;
        }
    }

    fullnameInput.addEventListener("input", function () {
        this.value = this.value.replace(/[^\p{L}\s]/gu, "");
        if (this.value.length > 50) this.value = this.value.slice(0, 50);

        const val = this.value.trim();
        if (val === "") {
            showError(fullnameInput, "Họ và tên không được để trống");
        } else if (!namePattern.test(val)) {
            showError(fullnameInput, "Vui lòng nhập đúng định dạng (chỉ chữ cái, không số hoặc ký tự đặc biệt)");
        } else {
            showError(fullnameInput, "");
        }
    });

    fullnameInput.addEventListener("blur", validateFullname);

    phoneInput.addEventListener("input", function () {
        this.value = this.value.replace(/[^0-9]/g, "");
        if (this.value.length > 10) this.value = this.value.slice(0, 10);

        if (this.value.length === 0) {
            showError(phoneInput, "Số điện thoại không được để trống");
        } else if (this.value.length < 10) {
            showError(phoneInput, "Vui lòng nhập đủ 10 chữ số");
        } else if (!phonePattern.test(this.value)) {
            showError(phoneInput, "Số điện thoại không đúng định dạng (03,05,07,08,09 + 8 số)");
        } else {
            showError(phoneInput, "");
        }
    });

    updateBtn.addEventListener("click", function (e) {
        clearErrors();
        let isValid = true;

        if (!validateFullname()) isValid = false;

        if (!phoneInput.value.trim() || !phonePattern.test(phoneInput.value.trim())) {
            showError(phoneInput, "Số điện thoại không hợp lệ");
            isValid = false;
        }

        if (!emailPattern.test(emailInput.value.trim())) {
            showError(emailInput, "Email không hợp lệ");
            isValid = false;
        }

        const birthday = birthdayInput.value.trim();
        if (birthday === "") {
            showError(birthdayInput, "Ngày sinh không được để trống");
            isValid = false;
        }

        const gender = document.querySelector('input[name="gender"]:checked');
        if (!gender) {
            showError(container.querySelector(".gender-container"), "Vui lòng chọn giới tính");
            isValid = false;
        }
        if (!isValid) {
            e.preventDefault();
            return;
        }
    });

    const addAddressBtn = document.getElementById("addAddressBtn");
    const addressContainer = document.getElementById("addressContainer");

    if (addAddressBtn && addressContainer) {

        function reindexAddresses() {
            const rows = addressContainer.querySelectorAll(".address-row");
            rows.forEach((row, index) => {
                const label = row.querySelector(".address-label");
                if (label) label.textContent = `Địa chỉ ${index + 1}`;
            });
        }

        function createAddressRow(index) {
            const newRow = document.createElement("div");
            newRow.classList.add("address-row");
            newRow.innerHTML = `
            <span class="address-label">Địa chỉ ${index}</span>
            <input type="text" name="city[]" placeholder="Tỉnh / Thành phố" class="address-input city-input">
            <input type="text" name="district[]" placeholder="Quận / Huyện" class="address-input district-input">
            <input type="text" name="street[]" placeholder="Đường / Số nhà" class="address-input street-input">

            <input type="hidden" name="addressId[]" value="0">
            <input type="hidden" name="isDefault[]" value="0">
            <input type="hidden" name="isDeleted[]" value="0">

            <button type="button" class="btn-remove">–</button>
            <button type="button" class="btn-default-address" style="display:none;">Địa chỉ mặc định</button>
            <button type="button" class="btn-default">Đặt làm mặc định</button>
        `;

            attachRowEvents(newRow);
            return newRow;
        }

        function attachRowEvents(row) {
            const removeBtn = row.querySelector(".btn-remove");
            const defaultBtn = row.querySelector(".btn-default");
            const defaultLabel = row.querySelector(".btn-default-address");

            removeBtn.addEventListener("click", function () {
                const totalRows = addressContainer.querySelectorAll(".address-row").length;

                if (totalRows === 1) {
                    alert("Phải có ít nhất một địa chỉ, không thể xóa dòng cuối cùng!");
                    return;
                }

                const isDefault = row.querySelector('input[name="isDefault[]"]').value;
                if (isDefault === "1") {
                    alert("Không thể xóa địa chỉ mặc định! Hãy đặt địa chỉ khác làm mặc định trước.");
                    return;
                }

                if (confirm("Bạn có chắc muốn xóa địa chỉ này không?")) {

                    row.querySelector('input[name="isDeleted[]"]').value = "1";

                    row.style.display = "none";

                    reindexAddresses();
                }
            });

            defaultBtn.addEventListener("click", function () {
                addressContainer.querySelectorAll(".address-row").forEach(r => {
                    r.classList.remove("default");
                    r.querySelector(".btn-default-address").style.display = "none";
                    r.querySelector(".btn-default").style.display = "inline-flex";
                    r.querySelector('input[name="isDefault[]"]').value = "0";
                });

                row.classList.add("default");
                defaultBtn.style.display = "none";
                defaultLabel.style.display = "inline-flex";

                row.querySelector('input[name="isDefault[]"]').value = "1";
            });

        }

        addAddressBtn.addEventListener("click", function () {
            const inputs = addressContainer.querySelectorAll(".street-input");
            const lastInput = inputs[inputs.length - 1];
            if (!lastInput || lastInput.value.trim() === "") {
                alert("Vui lòng nhập địa chỉ trước khi thêm mới!");
                return;
            }

            const newIndex = addressContainer.querySelectorAll(".address-row").length + 1;
            const newRow = createAddressRow(newIndex);
            addressContainer.appendChild(newRow);
            reindexAddresses();
        });

        addressContainer.querySelectorAll(".address-row").forEach(row => attachRowEvents(row));
    }

});