// Xử lý đăng nhập Admin
function loginAdmin() {
  const user = document.getElementById("username").value.trim();
  const pass = document.getElementById("password").value.trim();
  const errorMsg = document.getElementById("errorMsg");

  // Reset lỗi mỗi lần bấm nút
  errorMsg.style.display = "none";

  // Kiểm tra rỗng
  if (!user || !pass) {
    errorMsg.textContent = "Vui lòng nhập đầy đủ thông tin.";
    errorMsg.style.display = "block";
    return;
  }

  // DEMO: login đơn giản
  // Sếp sẽ thay đoạn này bằng API thật (fetch POST)
  if (user === "admin" && pass === "admin123") {
    // Chuyển sang dashboard sau khi đăng nhập thành công
    window.location.href = "/Admin/Dashboard.html";
  } else {
    errorMsg.textContent = "Sai tài khoản hoặc mật khẩu.";
    errorMsg.style.display = "block";
  }
}

// Xử lý khi bấm "Quên mật khẩu?"
function forgotPassword(e) {
  e.preventDefault();
  window.location.href = "/Admin/ForgotPassword.html";
}

// ============================= //
//   FILE DÙNG CHUNG CHO CẢ 2    //
//   TRANG LOGIN & FORGOT PASS   //
// ============================= //

// Gửi yêu cầu khôi phục mật khẩu (trang ForgotPassword.html)
function sendRecovery() {
  const input = document.getElementById("recoverIdentifier");
  const text = input ? input.value.trim() : "";
  const msg = document.getElementById("recoverMsg");

  if (!input) {
    // Không có input -> đang ở trang login -> bỏ qua
    return;
  }

  if (!text) {
    alert("Vui lòng nhập email hoặc tên đăng nhập.");
    return;
  }

  // DEMO: gửi yêu cầu khôi phục
  // Thực tế sẽ gọi API như:
  // fetch('/api/auth/recover', { method: 'POST', body: JSON.stringify({ identifier: text }) })
  msg.style.display = "block";
  msg.textContent = "Yêu cầu khôi phục đã được gửi (demo).";
}
