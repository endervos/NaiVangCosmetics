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
    infinite: true,
    speed: 600,
    cssEase: 'ease-in-out'
  });
});
document.addEventListener("DOMContentLoaded", () => {
  let isLoggedIn = false;
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