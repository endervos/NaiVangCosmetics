// ======= Format tiền =======
function formatMoney(num) {
  return num.toLocaleString("vi-VN") + "đ";
}

// ======= Tính tổng tạm tính từ sản phẩm =======
function calcSubtotal() {
  let subtotal = 0;
  const products = document.querySelectorAll(".product-item");

  products.forEach(product => {
    const qty = parseInt(product.querySelector(".qty").innerText);
    const unitPrice = parseInt(product.querySelector(".unit-price").dataset.price);
    subtotal += qty * unitPrice;
  });

  return subtotal;
}

// ======= Cập nhật Bill =======
function updateTotal() {
  let subtotal = calcSubtotal();
  let shipping = 30000; // phí ship cố định
  let discount = currentDiscount;

  document.getElementById("subtotal").innerText = formatMoney(subtotal);
  document.getElementById("shipping").innerText = formatMoney(shipping);
  document.getElementById("discount").innerText = "-" + formatMoney(discount);
  document.getElementById("total").innerText = formatMoney(subtotal + shipping - discount);
}

// ======= Toast Notification =======
function showToast(message, type = "info") {
  const toast = document.getElementById("toast");
  toast.innerText = message;

  // đổi màu theo loại
  if (type === "error") {
    toast.style.backgroundColor = "#e74c3c"; // đỏ
  } else if (type === "success") {
    toast.style.backgroundColor = "#27ae60"; // xanh lá
  } else {
    toast.style.backgroundColor = "#333"; // mặc định
  }

  toast.className = "show";
  setTimeout(() => {
    toast.className = toast.className.replace("show", "");
  }, 3000);
}

// ======= Mã giảm giá =======
let currentDiscount = 0;
document.getElementById("apply-discount").addEventListener("click", () => {
  const code = document.getElementById("discount-code").value.trim().toUpperCase();
  let subtotal = calcSubtotal();
  let shipping = 30000;

  if (code === "SALE10") {
    currentDiscount = 100000;
    showToast("Áp dụng mã giảm 100.000đ thành công!", "success");
  } else if (code === "FREESHIP") {
    currentDiscount = shipping;
    showToast("Áp dụng mã miễn phí vận chuyển!", "success");
  } else if (code === "VIP20") {
    currentDiscount = Math.floor(subtotal * 0.2);
    showToast("Áp dụng giảm 20% cho đơn hàng!", "success");
  } else {
    currentDiscount = 0;
    showToast("Mã giảm giá không hợp lệ", "error");
  }
  updateTotal();
});

// ======= Thanh toán =======
document.getElementById("pay-btn").addEventListener("click", () => {
  const payment = document.querySelector("input[name='payment']:checked").value;
  showToast(`Thanh toán thành công bằng phương thức: ${payment.toUpperCase()}`, "success");
});

// ======= Modal địa chỉ nâng cao =======
const modal = document.getElementById("address-modal");
const btnChange = document.getElementById("change-address");
const btnCancel = document.getElementById("cancel-modal");
const btnSave = document.getElementById("save-modal");
const btnAddNew = document.getElementById("add-new-address");
const newForm = document.getElementById("new-address-form");
const addressList = document.getElementById("address-list");

btnChange.addEventListener("click", () => {
  modal.style.display = "flex"; // hiện modal
  newForm.style.display = "none"; // ẩn form mới mỗi lần mở
});

btnCancel.addEventListener("click", () => {
  modal.style.display = "none";
});

btnAddNew.addEventListener("click", () => {
  newForm.style.display = "block"; // hiện form nhập địa chỉ mới
});

btnSave.addEventListener("click", () => {
  // Nếu đang thêm địa chỉ mới
  if (newForm.style.display === "block") {
    const fullname = document.getElementById("fullname").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const street = document.getElementById("street").value.trim();
    const city = document.getElementById("city").value.trim();
    const province = document.getElementById("province").value.trim();

    if (fullname && phone && street && city && province) {
      const newAddress = `${fullname} | ${phone} | ${street}, ${city}, ${province}`;
      
      // Thêm vào danh sách địa chỉ
      const div = document.createElement("div");
      div.classList.add("address-item");
      div.innerHTML = `<input type="radio" name="address" value="${newAddress}" checked> <span>${newAddress}</span>`;
      addressList.appendChild(div);

      // Cập nhật text chính
      document.getElementById("address-text").innerText = newAddress;
      modal.style.display = "none";
      showToast("Thêm địa chỉ thành công!", "success");
    } else {
      showToast("Vui lòng nhập đầy đủ thông tin địa chỉ!", "error");
    }
  } else {
    // Nếu chọn từ danh sách có sẵn
    const selected = document.querySelector("input[name='address']:checked");
    if (selected) {
      document.getElementById("address-text").innerText = selected.value;
      showToast("Cập nhật địa chỉ thành công!", "success");
    }
    modal.style.display = "none";
  }
});

// Ấn ngoài modal để đóng
window.addEventListener("click", (e) => {
  if (e.target === modal) {
    modal.style.display = "none";
  }
});

// ======= Khởi chạy ban đầu =======
updateTotal();
