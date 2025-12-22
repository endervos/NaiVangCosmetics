document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const errorMsg = document.getElementById('errorMsg');
    const successMsg = document.getElementById('successMsg');
    const loginBtn = document.getElementById('loginBtn');
    const btnText = document.getElementById('btnText');
    const btnLoading = document.getElementById('btnLoading');
    const loginUrl = loginForm.getAttribute('data-login-url') || '/manager/login-tthhn';
    const EMAIL_PATTERN = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const UPPERCASE_PATTERN = /[A-Z]/;
    const SPECIAL_CHAR_PATTERN = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;

    function isValidEmail(email) {
        return EMAIL_PATTERN.test(email);
    }

    function validatePassword(password) {
        if (password.length < 15) {
            return "Mật khẩu phải có ít nhất 15 ký tự";
        }
        if (!UPPERCASE_PATTERN.test(password)) {
            return "Mật khẩu phải có ít nhất 1 chữ hoa";
        }
        if (!SPECIAL_CHAR_PATTERN.test(password)) {
            return "Mật khẩu phải có ít nhất 1 ký tự đặc biệt";
        }
        return null;
    }

    function showFieldError(input, message) {
        input.classList.add('input-error');
        let errorDiv = input.parentElement.querySelector('.field-error');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'field-error';
            input.parentElement.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }

    function clearFieldError(input) {
        input.classList.remove('input-error');
        const errorDiv = input.parentElement.querySelector('.field-error');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }

    function clearAllErrors() {
        clearFieldError(usernameInput);
        clearFieldError(passwordInput);
        hideMessages();
    }

    usernameInput.addEventListener('blur', function() {
        const email = this.value.trim();
        if (email && !isValidEmail(email)) {
            showFieldError(this, 'Định dạng email không đúng');
        } else {
            clearFieldError(this);
        }
    });

    usernameInput.addEventListener('input', function() {
        if (this.classList.contains('input-error')) {
            clearFieldError(this);
        }
    });

    passwordInput.addEventListener('blur', function() {
        const password = this.value;
        if (password) {
            const error = validatePassword(password);
            if (error) {
                showFieldError(this, error);
            } else {
                clearFieldError(this);
            }
        }
    });

    passwordInput.addEventListener('input', function() {
        if (this.classList.contains('input-error')) {
            clearFieldError(this);
        }
    });

    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        const username = usernameInput.value.trim();
        const password = passwordInput.value;
        clearAllErrors();
        let hasError = false;
        if (!username) {
            showFieldError(usernameInput, 'Email không được để trống');
            hasError = true;
        } else if (!isValidEmail(username)) {
            showFieldError(usernameInput, 'Định dạng email không đúng');
            hasError = true;
        }
        if (!password) {
            showFieldError(passwordInput, 'Mật khẩu không được để trống');
            hasError = true;
        } else {
            const passwordError = validatePassword(password);
            if (passwordError) {
                showFieldError(passwordInput, passwordError);
                hasError = true;
            }
        }
        if (hasError) {
            return;
        }
        setLoading(true);
        try {
            const response = await fetch(loginUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password })
            });
            const data = await response.json();
            if (data.success) {
                showSuccess('Đăng nhập thành công! Đang chuyển hướng...');
                setTimeout(() => {
                    window.location.href = data.redirectUrl;
                }, 1000);
            } else {
                showError(data.message || 'Sai tài khoản hoặc mật khẩu');
                setLoading(false);
            }
        } catch (error) {
            console.error('Login error:', error);
            showError('Có lỗi xảy ra. Vui lòng thử lại!');
            setLoading(false);
        }
    });

    function setLoading(loading) {
        loginBtn.disabled = loading;
        btnText.style.display = loading ? 'none' : 'inline';
        btnLoading.style.display = loading ? 'inline' : 'none';
    }

    function showError(message) {
        errorMsg.textContent = message;
        errorMsg.style.display = 'block';
        successMsg.style.display = 'none';
    }

    function showSuccess(message) {
        successMsg.textContent = message;
        successMsg.style.display = 'block';
        errorMsg.style.display = 'none';
    }

    function hideMessages() {
        errorMsg.style.display = 'none';
        successMsg.style.display = 'none';
    }

    window.forgotPassword = function(event) {
        event.preventDefault();
        alert('Chức năng quên mật khẩu đang được phát triển!');
    };
});