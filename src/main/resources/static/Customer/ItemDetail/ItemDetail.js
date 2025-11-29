
document.addEventListener("DOMContentLoaded", () => {
  // Stars rating
  const starsEl = document.querySelector('.stars');
  const rating = parseFloat(starsEl.dataset.rating);
  starsEl.textContent = rating + " ⭐";

  // Thumbnail click → đổi ảnh chính
  const thumbnails = document.querySelectorAll('.thumbnail');
  const currentImage = document.getElementById('currentImage');

  thumbnails.forEach(thumb => {
    thumb.addEventListener('click', function() {
      // Đổi ảnh chính
      currentImage.src = this.src;

      // Reset active
      thumbnails.forEach(t => t.classList.remove('active'));
      this.classList.add('active');
    });
  });
});


document.addEventListener("DOMContentLoaded", () => {
  const wishlist = document.querySelector(".wishlist");
  const icon = document.getElementById("wishlistIcon");

  wishlist.addEventListener("click", () => {
    wishlist.classList.toggle("active");

    if (wishlist.classList.contains("active")) {
      // Đổi sang trái tim đỏ (solid)
      icon.classList.remove("fa-regular");
      icon.classList.add("fa-solid");
    } else {
      // Đổi về trái tim viền (đen)
      icon.classList.remove("fa-solid");
      icon.classList.add("fa-regular");
    }
  });
});

//rating
document.addEventListener("DOMContentLoaded", () => {
    const starsEl = document.querySelector('.stars');
    const rating = parseFloat(starsEl.dataset.rating); // ví dụ: 4.2
    starsEl.textContent = rating + " ⭐";
});

//Tabs
document.addEventListener("DOMContentLoaded", function () {
  const tabs = document.querySelectorAll(".tab-btn");
  const contents = document.querySelectorAll(".tab-content");

  tabs.forEach(tab => {
    tab.addEventListener("click", () => {
      // Xóa active ở tất cả tab và nội dung
      tabs.forEach(t => t.classList.remove("active"));
      contents.forEach(c => c.classList.remove("active"));

      // Thêm active cho tab vừa bấm
      tab.classList.add("active");
      document.getElementById(tab.dataset.tab).classList.add("active");
    });
  });
});

// Đăng nhập - ĐX
document.addEventListener("DOMContentLoaded", () => {
  let isLoggedIn = false; // mặc định chưa đăng nhập

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

document.addEventListener("DOMContentLoaded", () => {
    const stockEl   = document.querySelector(".stock-status");
    const qtyInput  = document.getElementById("quantity");
    const minusBtn  = document.getElementById("qtyMinus");
    const plusBtn   = document.getElementById("qtyPlus");
    const addBtn    = document.querySelector(".add-cart");
    const buyBtn    = document.querySelector(".buy-now");

    if (!stockEl || !qtyInput) return;

    const stock = Number.parseInt(stockEl.dataset.stock, 10);

    if (Number.isNaN(stock)) {
        console.warn("data-stock không hợp lệ!", stockEl.dataset.stock);
        return;
    }

    if (stock <= 0) {
        stockEl.textContent = "Hết hàng";

        qtyInput.value = 0;
        qtyInput.disabled = true;

        minusBtn.disabled = true;
        plusBtn.disabled  = true;

        addBtn.disabled  = true;
        buyBtn.disabled  = true;
        return;
    }

    stockEl.textContent = `Còn ${stock} sản phẩm`;

    qtyInput.disabled = false;
    qtyInput.value = 1;
    qtyInput.min = 1;
    qtyInput.max = stock;

    minusBtn.disabled = false;
    plusBtn.disabled = false;

    minusBtn.addEventListener("click", () => {
        let val = Number(qtyInput.value);
        if (val > 1) qtyInput.value = val - 1;
    });

    plusBtn.addEventListener("click", () => {
        let val = Number(qtyInput.value);
        if (val < stock) qtyInput.value = val + 1;
    });

    qtyInput.addEventListener("input", () => {
        let val = Number(qtyInput.value);

        if (Number.isNaN(val) || val < 1) val = 1;
        if (val > stock) val = stock;

        qtyInput.value = val;
    });
});
