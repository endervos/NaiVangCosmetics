document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('manager-info-form');

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
    const managerName = document.getElementById('name').value.trim();
    const managerDob = document.getElementById('dateOfBirth').value;
    const managerPhone = document.getElementById('phone').value.trim();
    const managerGender = document.querySelector('input[name="gender"]:checked');

    let isValid = true;
    let errorMessage = '';

    if (!managerName) {
      isValid = false;
      errorMessage += 'Họ và tên không được để trống.\n';
    }

    if (!managerDob) {
      isValid = false;
      errorMessage += 'Ngày tháng năm sinh không được để trống.\n';
    }

    if (!managerPhone || !/^\d{10}$/.test(managerPhone)) {
      isValid = false;
      errorMessage += 'Số điện thoại phải là 10 chữ số.\n';
    }

    if (!managerGender) {
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