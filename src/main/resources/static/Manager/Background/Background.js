document.addEventListener("DOMContentLoaded", () => {
  const userInfo = document.querySelector(".user-info");
  const dropdown = document.querySelector(".dropdown");

  userInfo.addEventListener("click", () => {
    dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
  });

  // Đóng dropdown khi click ngoài
  document.addEventListener("click", (e) => {
    if (!userInfo.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.style.display = "none";
    }
  });
});
