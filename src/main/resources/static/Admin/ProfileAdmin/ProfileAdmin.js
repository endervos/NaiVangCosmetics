
document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('manager-info-form');

  // Hàm hiển thị toast notification
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
    event.preventDefault(); // Ngăn hành vi submit mặc định

    // Lấy giá trị từ các trường
    const managerId = document.getElementById('manager-id').value;
    const managerName = document.getElementById('manager-name').value.trim();
    const managerEmail = document.getElementById('manager-email').value.trim();
    const managerDob = document.getElementById('manager-dob').value;
    const managerPhone = document.getElementById('manager-phone').value.trim();
    const managerGender = document.querySelector('input[name="manager-gender"]:checked').value;

    // Xác thực dữ liệu
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

    if (!isValid) {
      showToast(errorMessage, 'error');
      return;
    }

    // Tạo object dữ liệu để gửi
    const formData = {
      id: managerId,
      name: managerName,
      email: managerEmail,
      dob: managerDob,
      gender: managerGender,
      phone: managerPhone
    };

    // Giả lập gửi dữ liệu đến server
    console.log('Dữ liệu gửi đi:', formData);
    showToast('Thông tin đã được lưu thành công!', 'success');

    // Có thể thêm logic gửi dữ liệu đến API thực tế ở đây
    /*
    fetch('/api/manager/update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      },
      body: JSON.stringify(formData)
    })
    .then(response => {
      if (!response.ok) throw new Error('Lỗi cập nhật thông tin');
      return response.json();
    })
    .then(data => {
      showToast('Thông tin đã được lưu thành công!', 'success');
    })
    .catch(error => {
      showToast('Có lỗi xảy ra khi lưu thông tin: ' + error.message, 'error');
    });
    */
  });
});
