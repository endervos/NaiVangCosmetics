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
            dateFormat: "Y-m-d",       // format thực tế để gửi về server
            altInput: true,            // hiển thị format dễ đọc cho người dùng
            altFormat: "d/m/Y",        // định dạng hiển thị ra UI
            locale: "vn",              // ngôn ngữ Việt Nam
            maxDate: "today",          // không cho chọn ngày tương lai
            defaultDate: birthdayInput.value // lấy giá trị sẵn có trong input
        });
    }


    // 🔹 Regex chuẩn
    const namePattern = /^[\p{L}\s]{2,50}$/u;
    const phonePattern = /^(0[3|5|7|8|9])[0-9]{8}$/;
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const addressPattern = /^[A-Za-zÀ-ỹà-ỹĐđ0-9\s,./()-]{5,100}$/;

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
                if (response.redirected) {
                    console.log("✅ Server redirect →", response.url);
                    window.location.href = response.url;
                } else {
                    console.log("✅ Cập nhật thành công!");
                    alert("✅ Thông tin của bạn đã được cập nhật thành công!");
                }
            })

            .catch(err => {
                console.error("❌ Lỗi khi gửi dữ liệu:", err);
                alert("Có lỗi xảy ra khi cập nhật!");
            });
    });

    const addAddressBtn = document.getElementById("addAddressBtn");
    const addressContainer = document.getElementById("addressContainer");

    if (addAddressBtn && addressContainer) {

        // 🔹 Cập nhật lại chỉ số hiển thị (Địa chỉ 1, 2, ...)
        function reindexAddresses() {
            const rows = addressContainer.querySelectorAll(".address-row");
            rows.forEach((row, index) => {
                const label = row.querySelector(".address-label");
                if (label) label.textContent = `Địa chỉ ${index + 1}`;
            });
        }

        // 🔹 Hàm tạo dòng địa chỉ mới
        function createAddressRow(index) {
            const newRow = document.createElement("div");
            newRow.classList.add("address-row");
            newRow.innerHTML = `
            <span class="address-label">Địa chỉ ${index}</span>
            <input type="text" name="city[]" placeholder="Tỉnh / Thành phố" class="address-input city-input">
            <input type="text" name="district[]" placeholder="Quận / Huyện" class="address-input district-input">
            <input type="text" name="street[]" placeholder="Đường / Số nhà" class="address-input street-input">

            <button type="button" class="btn-remove">–</button>
            <button type="button" class="btn-default-address" style="display:none;">Địa chỉ mặc định</button>
            <button type="button" class="btn-default">Đặt làm mặc định</button>
        `;

            attachRowEvents(newRow);
            return newRow;
        }

        // 🔹 Gắn sự kiện cho mỗi dòng (remove + set default)
        function attachRowEvents(row) {
            const removeBtn = row.querySelector(".btn-remove");
            const defaultBtn = row.querySelector(".btn-default");
            const defaultLabel = row.querySelector(".btn-default-address");

            // ❌ Xóa dòng địa chỉ
            removeBtn.addEventListener("click", function () {
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

            // ⭐ Đặt làm mặc định
            defaultBtn.addEventListener("click", function () {
                // Reset tất cả dòng khác
                addressContainer.querySelectorAll(".address-row").forEach(r => {
                    r.classList.remove("default"); // bỏ highlight
                    r.querySelector(".btn-default-address").style.display = "none";
                    r.querySelector(".btn-default").style.display = "inline-flex";
                });

                // Đặt dòng hiện tại làm mặc định
                row.classList.add("default");
                defaultBtn.style.display = "none";
                defaultLabel.style.display = "inline-flex";
            });
        }

        // 🔹 Nút thêm địa chỉ
        addAddressBtn.addEventListener("click", function () {
            const inputs = addressContainer.querySelectorAll(".street-input");
            const lastInput = inputs[inputs.length - 1];
            if (!lastInput || lastInput.value.trim() === "") {
                alert("⚠️ Vui lòng nhập địa chỉ trước khi thêm mới!");
                return;
            }

            const newIndex = addressContainer.querySelectorAll(".address-row").length + 1;
            const newRow = createAddressRow(newIndex);
            addressContainer.appendChild(newRow);
            reindexAddresses();
        });

        // 🔹 Gắn event cho các dòng có sẵn (nếu có)
        addressContainer.querySelectorAll(".address-row").forEach(row => attachRowEvents(row));
    }

    fetch('/profile')
        .then(res => {
            if (!res.ok) throw new Error("API Profile lỗi hoặc không kết nối được!");
            return res.json();
        })
        .then(data => {
            if (!data || !data.addresses) {
                throw new Error("Profile không chứa thông tin địa chỉ!");
            }

            if (!Array.isArray(data.addresses)) {
                throw new Error("Dữ liệu address trong profile không phải danh sách!");
            }

            loadAddressFromDB(data.addresses);
        })
        .catch(err => {
            alert("Lỗi khi tải dữ liệu: " + err.message);
            console.error(err);
        });

});
