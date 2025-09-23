// Di chuyển con trỏ sang ô tiếp theo khi người dùng nhập giá trị
function moveFocus(currentInput, nextInputId) {
  if (currentInput.value.length === 1) { // Nếu đã nhập 1 ký tự
    const nextInput = document.getElementById(nextInputId);
    if (nextInput) nextInput.focus(); // Di chuyển focus đến ô tiếp theo
  }
}

// Xử lý sự kiện keydown cho từng ô OTP
function handleKeyDown(event, currentInput, prevInputId, nextInputId) {
  if (event.key === 'Backspace') {
    if (currentInput.value === '') { // Nếu ô hiện tại rỗng
      const prevInput = document.getElementById(prevInputId);
      if (prevInput) {
        prevInput.focus(); // Di chuyển về ô trước
        event.preventDefault(); // Ngăn hành động mặc định
      }
    }
  } else if (currentInput.value.length === 1 && event.key.match(/[0-9]/)) {
    // Nếu nhập số và ô đã có 1 ký tự, di chuyển sang ô tiếp theo
    const nextInput = document.getElementById(nextInputId);
    if (nextInput) {
      nextInput.focus();
      nextInput.value = event.key; // Gán giá trị vừa nhập vào ô tiếp theo
      event.preventDefault(); // Ngăn giá trị được nhập vào ô hiện tại
    }
  }
}

// Kiểm tra trạng thái các ô OTP để hiển thị/ẩn nút Xác nhận
function checkOtpInputs() {
  const otpInputs = [];
  for (let i = 1; i <= 6; i++) {
    otpInputs.push(document.getElementById(`otp-${i}`).value);
  }
  const isAllFilled = otpInputs.every(input => input.length === 1);
  document.querySelector('.verifyBtn').hidden = !isAllFilled; // Sử dụng querySelector vì nút có class="verifyBtn"
}

// Reset form: làm trống các ô OTP, ẩn nút Xác nhận và xóa thông báo
function resetForm() {
  for (let i = 1; i <= 6; i++) {
    document.getElementById(`otp-${i}`).value = '';
  }
  document.querySelector('.verifyBtn').hidden = true;
  const msgElement = document.getElementById('msg');
  msgElement.textContent = ''; // Xóa thông báo
  msgElement.classList.remove('error'); // Xóa lớp error
}

// Gắn sự kiện input và keydown cho từng ô OTP
for (let i = 1; i <= 6; i++) {
  const input = document.getElementById(`otp-${i}`);
  input.addEventListener('input', () => {
    if (i < 6 && input.value.length === 1) {
      moveFocus(input, `otp-${i + 1}`);
    }
    checkOtpInputs(); // Kiểm tra trạng thái các ô OTP
  });
  input.addEventListener('keydown', (event) => {
    const prevInputId = i > 1 ? `otp-${i - 1}` : null;
    const nextInputId = i < 6 ? `otp-${i + 1}` : null;
    handleKeyDown(event, input, prevInputId, nextInputId);
    checkOtpInputs(); // Kiểm tra trạng thái sau mỗi keydown
  });
}

// Xử lý xác nhận OTP
document.querySelector('.verifyBtn').onclick = function () {
  const otp = [];
  for (let i = 1; i <= 6; i++) {
    otp.push(document.getElementById(`otp-${i}`).value); // Lấy giá trị OTP từ các ô
  }

  const otpValue = otp.join('');
  const msgElement = document.getElementById('msg');

  if (otpValue.length === 6) {
    if (otpValue === '123456') {
      window.location.href = '/home'; // Chuyển hướng đến trang chủ
    } else {
      msgElement.textContent = 'Mã OTP không đúng. Vui lòng thử lại!';
      msgElement.classList.add('error'); // Thêm lớp error cho thông báo lỗi
      resetForm(); // Reset form nếu OTP sai
      setTimeout(() => {
        msgElement.textContent = '';
        msgElement.classList.remove('error');
      }, 5000); // Xóa thông báo lỗi sau 5 giây
    }
  } else {
    msgElement.textContent = 'Vui lòng nhập đầy đủ mã OTP.';
    msgElement.classList.add('error'); // Thêm lớp error
    setTimeout(() => {
      msgElement.textContent = '';
      msgElement.classList.remove('error');
    }, 5000); // Xóa thông báo lỗi sau 5 giây
  }
};

// Gửi lại mã OTP
document.querySelector('.resendBtn').onclick = function () {
  resetForm(); // Reset form khi gửi lại mã
  const msgElement = document.getElementById('msg');
  msgElement.textContent = 'Mã OTP mới đã được gửi!';
  msgElement.classList.remove('error'); // Đảm bảo không có lớp error
  setTimeout(() => {
    msgElement.textContent = '';
    msgElement.classList.remove('error');
  }, 30000); // Xóa thông báo sau 30 giây
};

// Khởi tạo trạng thái ban đầu: ẩn nút Xác nhận
document.querySelector('.verifyBtn').hidden = true;