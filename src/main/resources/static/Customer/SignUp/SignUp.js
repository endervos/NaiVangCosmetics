document.addEventListener("DOMContentLoaded", function () {
    const container = document.querySelector(".account-info");
    if (!container) return;

    const updateBtn = container.querySelector(".btn");
    const fullnameInput = container.querySelector('[th\\:field="*{fullname}"]');
    const phoneInput = container.querySelector('[th\\:field="*{phoneNumber}"]');
    const emailInput = container.querySelector('[th\\:field="*{email}"]');
    const birthdayInput = container.querySelector('[th\\:field="*{birthday}"]');
    const genderInputs = container.querySelectorAll('input[th\\:field="*{gender}"]');
    const addressInputs = container.querySelectorAll(".address-input");
    const namePattern = /^[A-Za-zÀ-ỹà-ỹĐđ\s'-]{2,50}$/;
    const phonePattern = /^(0[3|5|7|8|9])[0-9]{8}$/;
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const addressPattern = /^[A-Za-zÀ-ỹà-ỹĐđ0-9\s,./()-]{5,100}$/;

    function showError(element, message) {
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
        container.querySelectorAll(".error").forEach(e => e.textContent = "");
    }

    updateBtn.addEventListener("click", function (e) {
        e.preventDefault();
        clearErrors();
        let isValid = true;

        const fullname = fullnameInput.value.trim();
        if (fullname === "") {
            showError(fullnameInput, "Họ và tên không được để trống");
            isValid = false;
        } else if (!namePattern.test(fullname)) {
            showError(fullnameInput, "Họ tên chỉ được chứa chữ cái và dấu cách");
            isValid = false;
        } else {
            fullnameInput.value = fullname
                .toLowerCase()
                .replace(/(^|\s)\S/g, l => l.toUpperCase());
        }

        const phone = phoneInput.value.trim();
        if (phone === "") {
            showError(phoneInput, "Số điện thoại không được để trống");
            isValid = false;
        } else if (!phonePattern.test(phone)) {
            showError(phoneInput, "Số điện thoại phải gồm 10 số và bắt đầu bằng 03, 05, 07, 08 hoặc 09");
            isValid = false;
        }

        const email = emailInput.value.trim();
        if (email === "") {
            showError(emailInput, "Email không được để trống");
            isValid = false;
        } else if (!emailPattern.test(email)) {
            showError(emailInput, "Email không hợp lệ");
            isValid = false;
        }

        const birthday = birthdayInput.value.trim();
        if (birthday === "") {
            showError(birthdayInput, "Ngày sinh không được để trống");
            isValid = false;
        } else {
            const dob = new Date(birthday);
            const today = new Date();
            if (dob >= today) {
                showError(birthdayInput, "Ngày sinh phải là ngày trong quá khứ");
                isValid = false;
            }
        }

        const genderChecked = Array.from(genderInputs).some(g => g.checked);
        if (!genderChecked) {
            const genderDiv = container.querySelector(".gender-container");
            showError(genderDiv, "Vui lòng chọn giới tính");
            isValid = false;
        }

        const addressList = container.querySelectorAll(".address-input");
        if (addressList.length === 0) {
            alert("Bạn cần nhập ít nhất 1 địa chỉ!");
            isValid = false;
        } else {
            addressList.forEach((input, idx) => {
                const val = input.value.trim();
                if (val === "") {
                    showError(input, `Địa chỉ ${idx + 1} không được để trống`);
                    isValid = false;
                } else if (!addressPattern.test(val)) {
                    showError(input, `Địa chỉ ${idx + 1} chứa ký tự không hợp lệ (chỉ cho phép chữ, số, ',', '.', '/', '-')`);
                    isValid = false;
                }
            });
        }

        if (isValid) {
            alert("Tất cả thông tin hợp lệ — tiến hành gửi về server!");
        }
    });
});
