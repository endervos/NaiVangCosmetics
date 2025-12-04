document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("loginBtn");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      window.location.href = "../trangdangnhap/trangdangnhap.html";
    });
  }
});

document.querySelectorAll('.clickmenu2col').forEach(item => {
  item.addEventListener('click', e => {
    e.preventDefault();
    item.parentElement.classList.toggle('active');
  });
});

document.querySelectorAll(".sort-bar button").forEach(btn => {
  btn.addEventListener("click", function() {
    document.querySelector(".sort-bar button.active")?.classList.remove("active");
    this.classList.add("active");
  });
});

document.addEventListener("DOMContentLoaded", () => {
  let isLoggedIn = false;

  const loginBtn   = document.getElementById("login-btn");
  const subcribeBtn   = document.getElementById("subcribe-btn");
  const logoutBtn  = document.getElementById("logout-btn");
  const accountInfo = document.getElementById("account-info");

  function updateMenu() {
    if (isLoggedIn) {
      loginBtn.style.display = "none";
      subcribeBtn.style.display = "none";
      accountInfo.style.display = "block";
      logoutBtn.style.display = "block";
    } else {
      loginBtn.style.display = "block";
      subcribeBtn.style.display = "block";
      accountInfo.style.display = "none";
      logoutBtn.style.display = "none";
    }
  }

  loginBtn.addEventListener("click", () => {
    isLoggedIn = true;
    updateMenu();
  });

  logoutBtn.addEventListener("click", (e) => {
    e.preventDefault();
    isLoggedIn = false;
    updateMenu();
  });

  updateMenu();
});

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("subscribeForm");
  const emailInput = document.getElementById("emailInput");
  const emailError = document.getElementById("emailError");

  form.addEventListener("submit", function(e) {
    e.preventDefault();

    const email = emailInput.value.trim();
    const regex = /^[^\s@]+@(gmail\.com|yahoo\.com|outlook\.com)$/i;

    if (!regex.test(email)) {
      emailError.textContent = "Vui lòng nhập email hợp lệ!";
      emailError.classList.add("show");
    } else {
      emailError.textContent = "";
      emailError.classList.remove("show");

      alert("Đăng ký thành công với email: " + email);
      form.reset();
    }
  });
});