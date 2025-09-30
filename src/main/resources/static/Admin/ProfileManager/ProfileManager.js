document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('manager-info-form');
  
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
      alert(errorMessage);
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
    alert('Thông tin đã được lưu thành công!');

    // Có thể thêm logic gửi dữ liệu đến API thực tế ở đây
    // Ví dụ: 
    /*
    fetch('/api/manager/update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData)
    })
    .then(response => response.json())
    .then(data => {
      alert('Thông tin đã được lưu thành công!');
    })
    .catch(error => {
      alert('Có lỗi xảy ra khi lưu thông tin: ' + error.message);
    });
    */
  });
});