$(document).ready(function(){
  $('.slider').slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: true,
    prevArrow: $('.nav.prev'),
    nextArrow: $('.nav.next'),
    autoplay: true,
    autoplaySpeed: 3000,
    dots: true,
    infinite: true,      // 🔑 loop vô hạn
    speed: 600,          // tốc độ chuyển slide
    cssEase: 'ease-in-out' // chuyển động mượt
  });
});
document.addEventListener("DOMContentLoaded", () => {
  let isLoggedIn = false; // mặc định chưa đăng nhập

  const loginBtn   = document.getElementById("login-btn");
  const subBtn     = document.getElementById("subcribe-btn");
  const logoutBtn  = document.getElementById("logout-btn");
  const accountInfo = document.getElementById("account-info");

  function updateMenu() {
    if (isLoggedIn) {
      loginBtn.style.display = "none";
      subBtn.style.display = "none";
      accountInfo.style.display = "block";
      logoutBtn.style.display = "block";
    } else {
      loginBtn.style.display = "block";
      subBtn.style.display = "block";
      accountInfo.style.display = "none";
      logoutBtn.style.display = "none";
    }
  }

  // click login
  loginBtn.addEventListener("click", () => {
    isLoggedIn = true;
    updateMenu();
  });

  // click logout
  logoutBtn.addEventListener("click", (e) => {
    e.preventDefault();
    isLoggedIn = false;
    updateMenu();
  });

  // gọi lần đầu
  updateMenu();
});

//Email không hợp lệ
document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("subscribeForm");
  const emailInput = document.getElementById("emailInput");
  const emailError = document.getElementById("emailError");

  form.addEventListener("submit", function(e) {
    e.preventDefault(); // chặn submit mặc định

    const email = emailInput.value.trim();
    const regex = /^[^\s@]+@(gmail\.com|yahoo\.com|outlook\.com)$/i;

    if (!regex.test(email)) {
      emailError.textContent = "Vui lòng nhập email hợp lệ!";
      emailError.classList.add("show");
    } else {
      emailError.textContent = "";
      emailError.classList.remove("show");

      // TODO: gửi email lên server (nếu cần)
      alert("Đăng ký thành công với email: " + email);
      form.reset();
    }
  });
});

