document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('admin-info-form');

  function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => {
      toast.classList.add('show');
    }, 100);
    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => {
        toast.remove();
      }, 300);
    }, 3000);
  }

  form.addEventListener('submit', function (event) {
    const adminName = document.getElementById('name').value.trim();
    const adminDob = document.getElementById('dateOfBirth').value;
    const adminPhone = document.getElementById('phone').value.trim();
    const adminGender = document.querySelector('input[name="gender"]:checked');

    let isValid = true;
    let errorMessage = '';

    if (!adminName) {
      isValid = false;
      errorMessage += 'Họ và tên không được để trống.\n';
    }

    if (!adminDob) {
      isValid = false;
      errorMessage += 'Ngày tháng năm sinh không được để trống.\n';
    }

    if (!adminPhone || !/^\d{10}$/.test(adminPhone)) {
      isValid = false;
      errorMessage += 'Số điện thoại phải là 10 chữ số.\n';
    }

    if (!adminGender) {
      isValid = false;
      errorMessage += 'Vui lòng chọn giới tính.\n';
    }

    if (!isValid) {
      event.preventDefault();
      showToast(errorMessage, 'error');
      return;
    }
  });
});