document.addEventListener("DOMContentLoaded", function () {
    const container = document.querySelector(".account-info");
    if (!container) return;

    const updateBtn = container.querySelector(".btn");
    const fullnameInput = container.querySelector("#fullname");
    const phoneInput = container.querySelector("#phoneNumber");
    const emailInput = container.querySelector("#email");
    const birthdayInput = container.querySelector("#birthday");
    const genderInputs = container.querySelectorAll('input[name="gender"]');

    // 🔹 Regex chuẩn
    const namePattern = /^[\p{L}\s]{2,50}$/u;
    const phonePattern = /^(0[3|5|7|8|9])[0-9]{8}$/;
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const addressPattern = /^[A-Za-zÀ-ỹà-ỹĐđ0-9\s]{5,100}$/;

    // 🔹 Danh sách quận / huyện theo tỉnh
    const districtsByCity = {
        "Hà Nội": ["Ba Đình", "Hoàn Kiếm", "Đống Đa", "Cầu Giấy", "Hai Bà Trưng", "Thanh Xuân", "Nam Từ Liêm", "Bắc Từ Liêm", "Tây Hồ", "Long Biên"],
        "Hồ Chí Minh": ["Quận 1", "Quận 3", "Quận 5", "Quận 7", "Gò Vấp", "Tân Bình", "Phú Nhuận", "Bình Thạnh", "Thủ Đức", "Bình Tân"],
        "Đà Nẵng": ["Hải Châu", "Thanh Khê", "Sơn Trà", "Ngũ Hành Sơn", "Liên Chiểu", "Cẩm Lệ"],
        "Hải Phòng": ["Hồng Bàng", "Ngô Quyền", "Lê Chân", "Kiến An", "Hải An", "Đồ Sơn", "Dương Kinh"],
        "Cần Thơ": ["Ninh Kiều", "Bình Thủy", "Cái Răng", "Ô Môn", "Thốt Nốt"]
    };

    // 🔹 Hàm hiển thị lỗi
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

    // 🔹 Validate fullname
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

    // 🔹 Giới hạn & realtime fullname
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

    // 🔹 Phone realtime
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
        e.preventDefault();
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

        const gender = document.querySelector('input[name="gender"]:checked')?.value || "";
        const genderDiv = container.querySelector(".gender-container");
        if (!gender) {
            showError(genderDiv, "Vui lòng chọn giới tính");
            isValid = false;
        } else showError(genderDiv, "");

        if (!isValid) return;

        // ✅ Định dạng tên
        const name = fullnameInput.value.trim().toLowerCase();
        fullnameInput.value = name.replace(/(^|\s)\S/g, l => l.toUpperCase());

        console.log("✅ Họ tên sau khi format:", fullnameInput.value);

        // ✅ Hiện xác nhận và gửi fetch
        if (!confirm("Bạn có chắc muốn cập nhật thông tin không?")) {
            return;
        }

        console.log("🔹 Nút 'Cập nhật thông tin' đã được nhấn!");

        const phone = phoneInput.value.trim();

        fetch("/profile/update", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                fullname: fullnameInput.value.trim(),
                phoneNumber: phone,
                birthday: birthday,
                gender: gender
            })
        })
            .then(response => {
                if (response.ok) {
                    alert("Thông tin đã được cập nhật thành công!");
                    console.log("Cập nhật thành công!");
                } else {
                    alert("Có lỗi khi cập nhật thông tin!");
                    console.error("Lỗi phản hồi:", response.status);
                }
            })
            .catch(err => {
                console.error("Lỗi khi gửi dữ liệu:", err);
                alert("Có lỗi xảy ra khi gửi dữ liệu!");
            });

    });

    // ================== 🔹 QUẢN LÝ THÊM / XÓA ĐỊA CHỈ 🔹 ==================
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

        // ✅ Tạo 1 dòng địa chỉ mới
        function createAddressRow(index) {
            const newRow = document.createElement("div");
            newRow.classList.add("address-row");
            newRow.innerHTML = `
                <span class="address-label">Địa chỉ ${index}</span>

                <select name="city[]" class="address-select city-select">
                    <option value="">-- Tỉnh / Thành phố --</option>
                    <option value="Hà Nội">Hà Nội</option>
                    <option value="Hồ Chí Minh">Hồ Chí Minh</option>
                    <option value="Đà Nẵng">Đà Nẵng</option>
                    <option value="Hải Phòng">Hải Phòng</option>
                    <option value="Cần Thơ">Cần Thơ</option>
                </select>

                <select name="district[]" class="address-select district-select">
                    <option value="">-- Quận / Huyện --</option>
                </select>

                <input type="text" name="street[]" placeholder="Nhập đường / số nhà..." class="address-input street-input">

                <button type="button" class="btn-remove">–</button>
            `;

            const removeBtn = newRow.querySelector(".btn-remove");
            const citySelect = newRow.querySelector(".city-select");
            const districtSelect = newRow.querySelector(".district-select");
            const streetInput = newRow.querySelector(".street-input");


            // 🔹 Xóa dòng
            removeBtn.addEventListener("click", function () {
                const totalRows = addressContainer.querySelectorAll(".address-row").length;
                if (totalRows === 1) {
                    alert("⚠️ Phải có ít nhất một địa chỉ, không thể xóa dòng cuối cùng!");
                    return;
                }
                if (confirm("Bạn có chắc muốn xóa địa chỉ này không?")) {
                    newRow.remove();
                    reindexAddresses();
                }
            });

            return newRow;
        }

        // ✅ Thêm dòng mới
        addAddressBtn.addEventListener("click", function () {
            const inputs = addressContainer.querySelectorAll(".street-input");
            const lastInput = inputs[inputs.length - 1];
            if (!lastInput || lastInput.value.trim() === "") {
                alert("⚠️ Vui lòng nhập địa chỉ trước khi thêm mới!");
                return;
            }

            const newIndex = inputs.length + 1;
            const newRow = createAddressRow(newIndex);
            addressContainer.appendChild(newRow);
            reindexAddresses();
        });

        // ✅ Xóa cho các dòng có sẵn
        addressContainer.querySelectorAll(".btn-remove").forEach(btn => {
            const row = btn.closest(".address-row");
            btn.addEventListener("click", function () {
                const totalRows = addressContainer.querySelectorAll(".address-row").length;
                if (totalRows === 1) {
                    alert("⚠️ Phải có ít nhất một địa chỉ, không thể xóa dòng cuối cùng!");
                    return;
                }
                if (confirm("Bạn có chắc muốn xóa địa chỉ này không?")) {
                    row.remove();
                    reindexAddresses();
                }
            });
        });

        // ✅ Gắn sự kiện change cho các city-select có sẵn trong HTML
        addressContainer.querySelectorAll(".city-select").forEach(citySelect => {
            const districtSelect = citySelect.closest(".address-row").querySelector(".district-select");

            citySelect.addEventListener("change", function () {
                const city = this.value;
                districtSelect.innerHTML = '<option value="">-- Quận / Huyện --</option>';

                if (districtsByCity[city]) {
                    districtsByCity[city].forEach(d => {
                        const opt = document.createElement("option");
                        opt.value = d;
                        opt.textContent = d;
                        districtSelect.appendChild(opt);
                    });
                }
            });
        });
    }
});
