(() => {
  "use strict";

  const toast = (msg, ms = 2000) => {
    const t = document.createElement("div");
    t.style.cssText = "position:fixed;left:50%;transform:translateX(-50%);bottom:20px;background:#111;color:#fff;padding:.6rem .9rem;border-radius:.5rem;z-index:9999";
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), ms);
  };

  const addError = (el, msg) => {
    removeError(el);
    el.setAttribute("aria-invalid", "true");
    el.setAttribute("aria-describedby", el.id + "-error");
    el.classList.add("nv-invalid");
    const hint = document.createElement("div");
    hint.className = "nv-error";
    hint.id = el.id + "-error";
    hint.textContent = msg;
    el.insertAdjacentElement("afterend", hint);
  };

  const removeError = (el) => {
    el.removeAttribute("aria-invalid");
    el.removeAttribute("aria-describedby");
    el.classList.remove("nv-invalid");
    const next = el.nextElementSibling;
    if (next && next.classList?.contains("nv-error")) next.remove();
  };

  const addGenderError = (container, msg) => {
    removeGenderError(container);
    const parent = container.parentElement; // Lấy <div class="mb-3">
    parent.setAttribute("aria-invalid", "true");
    parent.setAttribute("aria-describedby", "gender-error");
    const hint = document.createElement("div");
    hint.className = "nv-error";
    hint.id = "gender-error";
    hint.textContent = msg;
    container.insertAdjacentElement("afterend", hint); // Chèn lỗi ngay sau <div class="gender-container">
  };

  const removeGenderError = (container) => {
    const parent = container.parentElement;
    parent.removeAttribute("aria-invalid");
    parent.removeAttribute("aria-describedby");
    const next = container.nextElementSibling; // Lấy .nv-error sau <div class="gender-container">
    if (next && next.classList?.contains("nv-error")) next.remove();
  };

  const addTermsError = (container, msg) => {
    removeTermsError(container);
    const label = container.querySelector(".form-check-label");
    if (label) {
      container.setAttribute("aria-invalid", "true");
      container.setAttribute("aria-describedby", "terms-error");
      const hint = document.createElement("div");
      hint.className = "nv-error";
      hint.id = "terms-error";
      hint.textContent = msg;
      label.insertAdjacentElement("afterend", hint); // Chèn lỗi ngay sau <label>
    }
  };

  const removeTermsError = (container) => {
    container.removeAttribute("aria-invalid");
    container.removeAttribute("aria-describedby");
    const label = container.querySelector(".form-check-label");
    const next = label?.nextElementSibling; // Lấy .nv-error sau <label>
    if (next && next.classList?.contains("nv-error")) next.remove();
  };

  const isEmail = (v) => /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(String(v).trim());
  const isVNPhone = (v) => {
    const d = String(v).replace(/\D/g, "");
    if (d.startsWith("84")) return d.length === 11 || d.length === 12;
    if (d.startsWith("0")) return d.length === 10 || d.length === 11;
    return false;
  };
  const isAdult = (val, minAge = 13) => {
    const dob = new Date(val);
    if (Number.isNaN(dob.getTime())) return false;
    const t = new Date();
    let age = t.getFullYear() - dob.getFullYear();
    const m = t.getMonth() - dob.getMonth();
    if (m < 0 || (m === 0 && t.getDate() < dob.getDate())) age--;
    return age >= minAge;
  };
  const pwStrong = (v) => v.length >= 15 && /[A-Za-z]/.test(v) && /\d/.test(v);

  document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".form-card form");
    if (!form) return;

    const inputs = form.querySelectorAll("input:not(.gender-check-input)");
    const fullName = inputs[0];
    const phone = inputs[1];
    const email = inputs[2];
    const genderContainer = form.querySelector(".gender-container");
    const genderInputs = form.querySelectorAll(".gender-check-input");
    const dob = inputs[3];
    const pw = inputs[4];
    const pw2 = inputs[5];
    const terms = form.querySelector("#termsCheck");
    const termsContainer = form.querySelector(".form-check");

    // Gán ID cho các input để hỗ trợ aria-describedby
    fullName.id = fullName.id || "fullName";
    phone.id = phone.id || "phone";
    email.id = email.id || "email";
    dob.id = dob.id || "dob";
    pw.id = pw.id || "password";
    pw2.id = pw2.id || "passwordConfirm";
    terms.id = terms.id || "termsCheck";
    genderContainer.id = genderContainer.id || "genderContainer";
    termsContainer.id = termsContainer.id || "termsContainer";

    form.addEventListener("submit", (e) => {
      e.preventDefault();
      let ok = true;

      const need = (el, msg) => {
        if (!el || !String(el.value).trim()) { addError(el, msg); ok = false; }
        else removeError(el);
      };

      need(fullName, "Vui lòng nhập Họ và tên.");

      if (phone && !isVNPhone(phone.value)) {
        addError(phone, "Số điện thoại không hợp lệ.");
        ok = false;
      } else removeError(phone);

      if (email && !isEmail(email.value)) {
        addError(email, "Email không hợp lệ.");
        ok = false;
      } else removeError(email);

      if (genderContainer && !Array.from(genderInputs).some(input => input.checked)) {
        addGenderError(genderContainer, "Vui lòng chọn giới tính.");
        ok = false;
      } else removeGenderError(genderContainer);

      if (dob && !isAdult(dob.value)) {
        addError(dob, "Bạn cần đủ 13 tuổi để đăng ký.");
        ok = false;
      } else removeError(dob);

      if (pw && !pwStrong(pw.value)) {
        addError(pw, "Mật khẩu cần ít nhất 15 ký tự, có chữ và số.");
        ok = false;
      } else removeError(pw);

      if (pw2 && pw2.value !== pw.value) {
        addError(pw2, "Mật khẩu nhập lại không khớp.");
        ok = false;
      } else removeError(pw2);

      if (terms && !terms.checked) {
        addTermsError(termsContainer, "Vui lòng đồng ý Điều khoản và Chính sách.");
        ok = false;
      } else {
        removeTermsError(termsContainer);
      }

      if (!ok) {
        toast("Vui lòng kiểm tra lại thông tin.");
        const firstInvalid = form.querySelector(".nv-error");
        firstInvalid?.scrollIntoView({ behavior: "smooth", block: "center" });
        return;
      }

      toast("Đăng ký thành công! 🎉");
    });
  });
})();