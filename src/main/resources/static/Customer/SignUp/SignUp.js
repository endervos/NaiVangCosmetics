document.getElementById("signUpForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Ngừng gửi form khi có lỗi
    
    let isValid = true;

    // Reset các lỗi trước
    document.querySelectorAll('.error').forEach(function(errorDiv) {
        errorDiv.innerHTML = '';
    });

    // Kiểm tra họ tên
    let fullname = document.getElementById("fullname").value;
    if (fullname.trim() === "") {
        document.getElementById("fullnameError").innerHTML = "Họ và tên không được để trống.";
        isValid = false;
    }

    // Kiểm tra số điện thoại
    let phoneNumber = document.getElementById("phoneNumber").value;
    let phonePattern = /^[0-9]{10,11}$/;
    if (!phonePattern.test(phoneNumber)) {
        document.getElementById("phoneNumberError").innerHTML = "Số điện thoại không hợp lệ.";
        isValid = false;
    }

    // Kiểm tra email
    let email = document.getElementById("email").value;
    let emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    if (!emailPattern.test(email)) {
        document.getElementById("emailError").innerHTML = "Email không hợp lệ.";
        isValid = false;
    }

    // Kiểm tra giới tính
    let gender = document.querySelector('input[name="gender"]:checked');
    if (!gender) {
        document.getElementById("genderError").innerHTML = "Bạn phải chọn giới tính.";
        isValid = false;
    }

    // Kiểm tra ngày sinh
    let birthday = document.getElementById("birthday").value;
    if (!birthday) {
        document.getElementById("birthdayError").innerHTML = "Ngày sinh không được để trống.";
        isValid = false;
    }

    // Kiểm tra mật khẩu
    let password = document.getElementById("password").value;
    if (password.length < 15) {
        document.getElementById("passwordError").innerHTML = "Mật khẩu phải có ít nhất 15 ký tự.";
        isValid = false;
    }

    // Kiểm tra xác nhận mật khẩu
    let confirmPassword = document.getElementById("confirmPassword").value;
    if (confirmPassword !== password) {
        document.getElementById("confirmPasswordError").innerHTML = "Mật khẩu xác nhận không khớp.";
        isValid = false;
    }

    // Kiểm tra điều khoản
    let termsCheck = document.getElementById("termsCheck").checked;
    if (!termsCheck) {
        document.getElementById("termsError").innerHTML = "Bạn phải đồng ý với Điều khoản & Chính sách.";
        isValid = false;
    }

    // Nếu tất cả hợp lệ, có thể gửi form
    if (isValid) {
        alert("Đăng ký thành công!");
        // Dùng form.submit() nếu muốn gửi form sau khi xác nhận
        // this.submit();
    }
});
