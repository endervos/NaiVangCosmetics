const form = document.querySelector('.change-password-form');
const currentPassword = document.getElementById('current-password');
const newPassword = document.getElementById('new-password');
const confirmPassword = document.getElementById('confirm-password');
const warningMessage = document.querySelector('.warning-message');
const button = document.querySelector('.custom-button');

warningMessage.style.display = 'block';
warningMessage.textContent = 'Cảnh báo: Đảm bảo mật khẩu mới khác với mật khẩu cũ.';

const storedCurrentPassword = '1';

function validatePasswords(event) {
  event.preventDefault();
  if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
    warningMessage.textContent = 'Vui lòng điền đầy đủ các thông tin!!';
    return false;
  }
  if (currentPassword.value !== storedCurrentPassword) {
    warningMessage.textContent = 'Mật khẩu cũ không đúng!';
    return false;
  }
  if (newPassword.value === currentPassword.value) {
    warningMessage.textContent = 'Mật khẩu mới không được trùng với mật khẩu cũ!';
    return false;
  }
  if (newPassword.value !== confirmPassword.value) {
    warningMessage.textContent = 'Mật khẩu mới và mật khẩu xác nhận không khớp!';
    return false;
  } else {
    warningMessage.textContent = 'Cảnh báo: Đảm bảo mật khẩu mới khác với mật khẩu cũ.';
  }
  alert('Đổi mật khẩu thành công!');
  form.submit();
}

form.addEventListener('submit', validatePasswords);