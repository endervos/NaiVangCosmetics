(() => {
  "use strict";

  const onReady = (fn) => {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", fn);
    } else fn();
  };

  const toast = (msg, ms = 2200) => {
    const t = document.createElement("div");
    t.style.cssText = "position:fixed;left:50%;transform:translateX(-50%);bottom:20px;background:#111;color:#fff;padding:.6rem .9rem;border-radius:.5rem;z-index:9999";
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), ms);
  };

  const addError = (el, msg) => {
    removeError(el);
    el.setAttribute("aria-invalid", "true");
    el.classList.add("nv-invalid");
    const hint = document.createElement("div");
    hint.className = "nv-error";
    hint.textContent = msg;
    el.insertAdjacentElement("afterend", hint);
  };

  const removeError = (el) => {
    el.removeAttribute("aria-invalid");
    el.classList.remove("nv-invalid");
    const next = el.nextElementSibling;
    if (next && next.classList?.contains("nv-error")) next.remove();
  };

  // Validator functions
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

  onReady(() => {
    // 1) Tìm form đăng ký
    const form = document.querySelector(".form-card form") || document.querySelector("form");
    if (!form) { console.warn("Không tìm thấy form đăng ký!"); toast("Không tìm thấy form đăng ký!"); return; }

    // 2) Các input
    const inputs = form.querySelectorAll("input");
    const firstName = inputs[0];
    const lastName  = inputs[1];
    const phone     = inputs[2];
    const email     = form.querySelector('input[type="email"]') || inputs[3];
    const dob       = form.querySelector('input[type="date"]')  || inputs[4];
    const pw        = inputs[5];
    const pw2       = inputs[6];
    const captchaIn = form.querySelector(".captcha-input") || inputs[7];
    const captchaBx = form.querySelector(".captcha-box");
    const terms     = form.querySelector("#termsCheck") || inputs[8];

    // 3) CAPTCHA
    const genCap = () => {
      const chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
      return Array.from({ length: 6 }, () => chars[Math.floor(Math.random() * chars.length)]).join("");
    };

    const resetCaptcha = () => {
      if (captchaBx) {
        captchaBx.textContent = genCap();
        if (captchaIn) captchaIn.value = "";
      }
    };

    if (captchaBx) resetCaptcha();

    // 4) Validate khi submit
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      let ok = true;

      const need = (el, msg) => {
        if (!el || !String(el.value).trim()) { addError(el, msg); ok = false; }
        else removeError(el);
      };

      need(firstName, "Vui lòng nhập Họ.");
      need(lastName,  "Vui lòng nhập Tên.");

      if (phone) {
        if (!isVNPhone(phone.value)) { addError(phone, "Số điện thoại không hợp lệ."); ok = false; }
        else removeError(phone);
      }

      if (email) {
        if (!isEmail(email.value)) { addError(email, "Email không hợp lệ."); ok = false; }
        else removeError(email);
      }

      if (dob) {
        if (!isAdult(dob.value, 13)) { addError(dob, "Bạn cần đủ 13 tuổi để đăng ký."); ok = false; }
        else removeError(dob);
      }

      if (pw) {
        if (!pwStrong(pw.value)) { addError(pw, "Mật khẩu cần ít nhất 15 ký tự, có chữ và số."); ok = false; }
        else removeError(pw);
      }

      if (pw2) {
        if (pw?.value !== pw2.value) { addError(pw2, "Mật khẩu nhập lại không khớp."); ok = false; }
        else removeError(pw2);
      }

      if (captchaIn && captchaBx) {
        const exp = captchaBx.textContent.trim();
        const got = captchaIn.value.trim();
        if (!got || got !== exp) {
          addError(captchaIn, "CAPTCHA không đúng. Vui lòng nhập lại.");
          ok = false;
        } else removeError(captchaIn);
      }

      if (terms && !terms.checked) { terms.focus(); toast("Bạn cần đồng ý Điều khoản và Chính sách."); ok = false; }

      if (!ok) {
        toast("Vui lòng kiểm tra lại thông tin.");
        const firstInvalid = form.querySelector(".nv-error");
        firstInvalid?.scrollIntoView({ behavior: "smooth", block: "center" });
        resetCaptcha();
      } else {
        toast("Đăng ký thành công! 🎉");
        form.reset();
        resetCaptcha();
        setTimeout(() => {
          window.location.href = "/trangmaxacnhan/trangmaxacnhan.html";
        }, 2000);
      }
    });
  });
})();
