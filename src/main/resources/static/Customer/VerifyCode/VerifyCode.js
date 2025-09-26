// Di chuyển con trỏ sang ô tiếp theo khi người dùng nhập giá trị
function moveFocus(currentInput, nextInputId) {
  if (currentInput.value.length === 1) {
    const nextInput = document.getElementById(nextInputId);
    if (nextInput) nextInput.focus();
  }
}

// Xử lý sự kiện keydown cho từng ô OTP
function handleKeyDown(event, currentInput, prevInputId, nextInputId) {
  if (event.key === 'Backspace') {
    if (currentInput.value === '') {
      const prevInput = document.getElementById(prevInputId);
      if (prevInput) {
        prevInput.focus();
        event.preventDefault();
      }
    }
  } else if (currentInput.value.length === 1 && event.key.match(/[0-9]/)) {
    const nextInput = document.getElementById(nextInputId);
    if (nextInput) {
      nextInput.focus();
      nextInput.value = event.key;
      event.preventDefault();
    }
  }
}

// Kiểm tra đủ 6 số thì gán vào hidden input
function checkOtpInputs() {
  const otpInputs = [];
  for (let i = 1; i <= 6; i++) {
    otpInputs.push(document.getElementById(`digit${i}`).value);
  }
  const isAllFilled = otpInputs.every(input => input.length === 1);
  const verifyBtn = document.querySelector('.verifyBtn');
  verifyBtn.hidden = !isAllFilled;

  if (isAllFilled) {
    document.getElementById('otp-code').value = otpInputs.join('');
  }
}

// Gắn sự kiện cho từng ô OTP
for (let i = 1; i <= 6; i++) {
  const input = document.getElementById(`digit${i}`);
  input.addEventListener('input', () => {
    if (i < 6 && input.value.length === 1) {
      moveFocus(input, `digit${i + 1}`);
    }
    checkOtpInputs();
  });
  input.addEventListener('keydown', (event) => {
    const prevInputId = i > 1 ? `digit${i - 1}` : null;
    const nextInputId = i < 6 ? `digit${i + 1}` : null;
    handleKeyDown(event, input, prevInputId, nextInputId);
    checkOtpInputs();
  });
}

// Khởi tạo trạng thái ban đầu
document.querySelector('.verifyBtn').hidden = true;