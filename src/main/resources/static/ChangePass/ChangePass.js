// Lấy các phần tử trong form
const form = document.querySelector('.change-password-form');
const currentPassword = document.getElementById('current-password');
const newPassword = document.getElementById('new-password');
const confirmPassword = document.getElementById('confirm-password');
const warningMessage = document.querySelector('.warning-message');
const button = document.querySelector('.custom-button');

// Cảnh báo ban đầu vẫn hiển thị
warningMessage.style.display = 'block';
warningMessage.textContent = 'Cảnh báo: Đảm bảo mật khẩu mới khác với mật khẩu cũ.';

// Mật khẩu cũ giả định (có thể thay bằng giá trị từ server hoặc session)
const storedCurrentPassword = '1'; // Ví dụ mật khẩu cũ lưu trên server

// Hàm kiểm tra mật khẩu
function validatePasswords(event) {
  // Ngừng gửi form nếu có lỗi
  event.preventDefault();

  // Kiểm tra nếu mật khẩu cũ, mật khẩu mới hoặc mật khẩu xác nhận trống
  if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
    warningMessage.textContent = 'Vui lòng điền đầy đủ các thông tin!!';
    return false;
  }

  // Kiểm tra mật khẩu cũ
  if (currentPassword.value !== storedCurrentPassword) {
    warningMessage.textContent = 'Mật khẩu cũ không đúng!';
    return false;
  }

  // Kiểm tra mật khẩu mới có trùng với mật khẩu cũ không
  if (newPassword.value === currentPassword.value) {
    warningMessage.textContent = 'Mật khẩu mới không được trùng với mật khẩu cũ!';
    return false;
  }

  // Kiểm tra mật khẩu xác nhận có khớp với mật khẩu mới không
  if (newPassword.value !== confirmPassword.value) {
    warningMessage.textContent = 'Mật khẩu mới và mật khẩu xác nhận không khớp!';
    return false;
  } else {
    warningMessage.textContent = 'Cảnh báo: Đảm bảo mật khẩu mới khác với mật khẩu cũ.'; // Giữ lại cảnh báo ban đầu
  }

  // Nếu không có lỗi, hiển thị thông báo thành công và gửi form
  alert('Đổi mật khẩu thành công!');
  form.submit();
}

// Lắng nghe sự kiện submit của form
form.addEventListener('submit', validatePasswords);
