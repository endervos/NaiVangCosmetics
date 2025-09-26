(() => {
  "use strict";

  const onReady = (fn) => {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", fn);
    } else fn();
  };

  const toast = (msg, ms = 2200) => {
    const t = document.createElement("div");
    t.style.cssText =
      "position:fixed;left:50%;transform:translateX(-50%);bottom:20px;background:#111;color:#fff;padding:.6rem .9rem;border-radius:.5rem;z-index:9999";
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

  // Validator
  const isEmail = (v) => {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(v);
  };

  onReady(() => {
    const form = document.querySelector(".login-card form");
    if (!form) {
      console.warn("Không tìm thấy form đăng nhập!");
      toast("Không tìm thấy form đăng nhập!");
      return;
    }

    const email = form.querySelector('input[name="username"]');
    const password = form.querySelector('input[name="password"]');
    const captchaIn = form.querySelector(".captcha-input");
    const captchaBx = form.querySelector(".captcha-box");

    // Tạo CAPTCHA
    const genCap = () => {
      const chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
      let s = "";
      for (let i = 0; i < 6; i++) s += chars[Math.floor(Math.random() * chars.length)];
      return s;
    };

    const resetCaptcha = () => {
      if (captchaBx) {
        captchaBx.textContent = genCap();
        if (captchaIn) captchaIn.value = "";
      }
    };

    if (captchaBx) resetCaptcha();

    // Validate khi submit
    form.addEventListener("submit", (e) => {
      let ok = true;

      const need = (el, msg) => {
        if (!el || !String(el.value).trim()) {
          addError(el, msg);
          ok = false;
        } else removeError(el);
      };

      need(email, "Chưa nhập email.");
      if (email && !isEmail(email.value)) {
        addError(email, "Định dạng email không đúng.");
        ok = false;
      } else if (email) removeError(email);

      need(password, "Chưa nhập mật khẩu.");

      if (captchaIn && captchaBx) {
        const exp = captchaBx.textContent.trim();
        const got = captchaIn.value.trim();
        if (!got || got !== exp) {
          addError(captchaIn, "CAPTCHA sai.");
          resetCaptcha();
          ok = false;
        } else {
          removeError(captchaIn);
          resetCaptcha(); // reset CAPTCHA cho lần sau
        }
      }

      if (!ok) {
        e.preventDefault(); // chỉ chặn submit khi dữ liệu sai
        toast("Thông tin không hợp lệ. Vui lòng kiểm tra lại!");
        const firstInvalid = form.querySelector(".nv-error");
        if (firstInvalid) {
          firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });
        }
      }
    });
  });
})();
