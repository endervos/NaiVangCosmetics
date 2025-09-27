document.getElementById("signUpForm").addEventListener("submit", function(event) {
    event.preventDefault();
    let isValid = true;

    document.querySelectorAll('.error').forEach(div => div.innerHTML = '');

    let fullname = document.getElementById("fullname").value.trim();
    if (fullname === "") {
        document.getElementById("fullnameError").innerHTML = "Họ và tên không được để trống";
        isValid = false;
    }

    let phoneNumber = document.getElementById("phoneNumber").value.trim();
    let phonePattern = /^\d{10}$/;
    if (!phonePattern.test(phoneNumber)) {
        document.getElementById("phoneNumberError").innerHTML = "Số điện thoại phải bao gồm đúng 10 số";
        isValid = false;
    }

    let email = document.getElementById("email").value.trim();
    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailPattern.test(email)) {
        document.getElementById("emailError").innerHTML = "Email không hợp lệ";
        isValid = false;
    }

    let gender = document.querySelector('input[name="gender"]:checked');
    if (!gender) {
        document.getElementById("genderError").innerHTML = "Giới tính không được để trống";
        isValid = false;
    }

    let birthday = document.getElementById("birthday").value;
    if (!birthday) {
        document.getElementById("birthdayError").innerHTML = "Ngày sinh không được để trống";
        isValid = false;
    } else {
        let today = new Date();
        let dob = new Date(birthday);
        if (dob >= today) {
            document.getElementById("birthdayError").innerHTML = "Ngày sinh phải là ngày trong quá khứ";
            isValid = false;
        }
    }

    let password = document.getElementById("password").value;
    let passwordPattern = /^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{15,}$/;
    if (!passwordPattern.test(password)) {
        document.getElementById("passwordError").innerHTML = "Mật khẩu phải có ít nhất 15 ký tự, bao gồm ít nhất 1 chữ hoa và 1 ký tự đặc biệt";
        isValid = false;
    }

    let confirmPassword = document.getElementById("confirmPassword").value;
    if (confirmPassword !== password) {
        document.getElementById("confirmPasswordError").innerHTML = "Mật khẩu xác nhận không khớp";
        isValid = false;
    }

    let termsCheck = document.getElementById("termsCheck").checked;
    if (!termsCheck) {
        document.getElementById("termsError").innerHTML = "Bạn phải đồng ý với Điều khoản & Chính sách.";
        isValid = false;
    }

    if (isValid) {
        this.submit();
    }
});