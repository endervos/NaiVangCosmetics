// ======================== Toast Notification ========================
function showToast(message, type = "info") {
  const toast = document.getElementById("toast");
  toast.innerText = message;

  toast.style.backgroundColor =
    type === "error" ? "#e74c3c" :
    type === "success" ? "#27ae60" : "#333";

  toast.className = "show";
  setTimeout(() => toast.className = toast.className.replace("show", ""), 3000);
}

// ======================== Modal địa chỉ ========================
const modal = document.getElementById("address-modal");
const btnChange = document.getElementById("change-address");
const btnCancel = document.getElementById("cancel-modal");
const btnSave = document.getElementById("save-modal");
const btnAddNew = document.getElementById("add-new-address");
const newForm = document.getElementById("new-address-form");
const addressList = document.getElementById("address-list");

// Mở modal
btnChange.addEventListener("click", () => {
  modal.style.display = "flex";
  newForm.style.display = "none";
});

// Đóng modal
btnCancel.addEventListener("click", () => {
  modal.style.display = "none";
});

// Hiện form nhập mới
btnAddNew.addEventListener("click", () => {
  newForm.style.display = "block";
});

// ======= Nút Lưu =======
btnSave.addEventListener("click", async () => {
  if (newForm.style.display === "block") {
    const phone = document.getElementById("phone").value.trim();
    const street = document.getElementById("street").value.trim();
    const district = document.getElementById("city").value.trim();
    const city = document.getElementById("province").value.trim();

    if (!phone || !street || !district || !city) {
      showToast("Vui lòng nhập đầy đủ thông tin địa chỉ!", "error");
      return;
    }

    try {
      const res = await fetch("/cart/address/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ street, district, city, phoneNumber: phone })
      });

      if (!res.ok) throw new Error("Lỗi khi lưu địa chỉ");
      const data = await res.json();

      // Cập nhật giao diện
      const div = document.createElement("div");
      div.classList.add("address-item");
      div.innerHTML = `
        <input type="radio" name="address" data-id="${data.id}" value="${data.fullAddress}" checked>
        <span>${data.fullAddress}</span>`;
      addressList.appendChild(div);

      document.getElementById("address-text").innerText = data.fullAddress;
      modal.style.display = "none";
      showToast("Thêm và đặt địa chỉ mặc định thành công!", "success");
    } catch (err) {
      console.error(err);
      showToast("Không thể lưu địa chỉ!", "error");
    }

  } else {
    const selected = document.querySelector("input[name='address']:checked");
    if (!selected) {
      showToast("Vui lòng chọn một địa chỉ trước khi lưu!", "error");
      return;
    }

    const id = selected.getAttribute("data-id");
    const addressText = selected.value;

    try {
      const res = await fetch(`/cart/address/set-default/${id}`, { method: "POST" });
      if (!res.ok) throw new Error("Lỗi khi đặt địa chỉ mặc định");

      document.getElementById("address-text").innerText = addressText;
      showToast("Cập nhật địa chỉ mặc định thành công!", "success");
      modal.style.display = "none";
    } catch (err) {
      console.error(err);
      showToast("Không thể cập nhật địa chỉ mặc định!", "error");
    }
  }
});

// Đóng modal khi click ra ngoài
window.addEventListener("click", (e) => {
  if (e.target === modal) modal.style.display = "none";
});

// ======================== Áp dụng mã giảm giá ========================
const applyBtn = document.getElementById("apply-discount");
const voucherMsg = document.createElement("p");
voucherMsg.id = "voucher-message";
voucherMsg.style.marginTop = "5px";
voucherMsg.style.fontSize = "14px";
voucherMsg.style.transition = "0.3s";
document.querySelector(".checkout-discount").appendChild(voucherMsg);

applyBtn.addEventListener("click", async () => {
  const code = document.getElementById("discount-code").value.trim();

  if (!code) {
    voucherMsg.textContent = "Vui lòng nhập mã giảm giá!";
    voucherMsg.style.color = "red";
    return;
  }

  try {
    const res = await fetch(`/cart/apply-voucher?code=${encodeURIComponent(code)}`, {
      method: "POST"
    });

    if (!res.ok) throw new Error("Lỗi server");
    const data = await res.json();

    if (!data.valid) {
      voucherMsg.textContent = data.message;
      voucherMsg.style.color = "red";
      return;
    }

    const percent = data.discountPercent;

    const subtotalElem = document.querySelector(".checkout-summary p:nth-child(2) span");
    const discountElem = document.querySelector(".checkout-summary p:nth-child(4) span");
    const totalElem = document.querySelector(".checkout-summary .total span");

    const subtotal = parseInt(subtotalElem.innerText.replace(/[^\d]/g, ""));
    const discountValue = Math.floor(subtotal * percent / 100);
    const newTotal = subtotal - discountValue;

    discountElem.innerText = `-${discountValue.toLocaleString()}đ`;
    totalElem.innerText = `${newTotal.toLocaleString()}đ`;

    voucherMsg.textContent = `${data.message} (Giảm ${percent}%)`;
    voucherMsg.style.color = "green";

  } catch (err) {
    console.error(err);
    voucherMsg.textContent = "Không thể áp dụng mã giảm giá!";
    voucherMsg.style.color = "red";
  }
});