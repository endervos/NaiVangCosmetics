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

  const isEmail = (v) => {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(v);
  };

  onReady(() => {
    const form = document.querySelector(".login-card form");
    if (!form) {
      console.warn("Không tìm thấy form đăng nhập!");
      return;
    }

    const email = form.querySelector('input[name="username"]');
    const password = form.querySelector('input[name="password"]');
    const submitBtn = form.querySelector('button[type="submit"]');
    const captchaIn = form.querySelector(".captcha-input");
    const captchaBx = form.querySelector(".captcha-box");

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

    const setLoading = (loading) => {
      if (submitBtn) {
        submitBtn.disabled = loading;
        submitBtn.textContent = loading ? "Đang xử lý..." : "Đăng nhập";
      }
    };

    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      let ok = true;

      if (!email || !email.value.trim()) {
        addError(email, "Chưa nhập email.");
        ok = false;
      } else if (!isEmail(email.value.trim())) {
        addError(email, "Định dạng email không đúng.");
        ok = false;
      } else {
        removeError(email);
      }

      if (!password || !password.value.trim()) {
        addError(password, "Chưa nhập mật khẩu.");
        ok = false;
      } else {
        removeError(password);
      }

      if (captchaIn && captchaBx) {
        const exp = captchaBx.textContent.trim();
        const got = captchaIn.value.trim();
        if (!got || got !== exp) {
          addError(captchaIn, "CAPTCHA sai.");
          resetCaptcha();
          ok = false;
        } else {
          removeError(captchaIn);
        }
      }

      if (!ok) {
        toast("Thông tin không hợp lệ. Vui lòng kiểm tra lại!");
        const firstInvalid = form.querySelector(".nv-error");
        if (firstInvalid) {
          firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });
        }
        return;
      }

      setLoading(true);

      try {
        const loginUrl = form.getAttribute("action") || "/login";

        const response = await fetch(loginUrl, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            username: email.value.trim(),
            password: password.value,
          }),
        });

        const data = await response.json();

        if (data.success) {
          toast("Đăng nhập thành công! Đang chuyển hướng...");
          if (captchaBx) resetCaptcha();
          setTimeout(() => {
            window.location.href = data.redirectUrl || "/";
          }, 1000);
        } else {
          toast(data.message || "Đăng nhập thất bại!");

          if (email && data.message && data.message.includes("email")) {
            addError(email, data.message);
          } else if (password) {
            addError(password, data.message || "Email hoặc mật khẩu không đúng");
          }
          if (captchaBx) resetCaptcha();

          setLoading(false);
        }
      } catch (error) {
        console.error("Login error:", error);
        toast("Có lỗi xảy ra. Vui lòng thử lại!");
        setLoading(false);
        if (captchaBx) resetCaptcha();
      }
    });

    if (email) {
      email.addEventListener("input", () => removeError(email));
    }
    if (password) {
      password.addEventListener("input", () => removeError(password));
    }
    if (captchaIn) {
      captchaIn.addEventListener("input", () => removeError(captchaIn));
    }
  });
})();