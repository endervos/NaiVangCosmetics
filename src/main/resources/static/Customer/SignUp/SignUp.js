(() => {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".form-card form");
    if (!form) return;

    form.addEventListener("submit", (e) => {
      e.preventDefault();
      console.log("Form submitted!");
    });
  });
})();