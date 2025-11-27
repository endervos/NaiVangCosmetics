document.addEventListener("DOMContentLoaded", () => {
  console.log("Review Management loaded!");

  const userInfo = document.querySelector(".user-info");
  const dropdown = document.querySelector(".dropdown");

  if (userInfo && dropdown) {
    userInfo.addEventListener("click", (e) => {
      e.stopPropagation();
      dropdown.style.display =
        dropdown.style.display === "block" ? "none" : "block";
    });

    document.addEventListener("click", (e) => {
      if (!userInfo.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.style.display = "none";
      }
    });
  }

  const logoutBtn = document.querySelector(".logout-btn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault();
      if (confirm("Bạn muốn đăng xuất?")) {
        window.location.href = "/logout";
      }
    });
  }

  const applyFilterBtn = document.getElementById("apply-filter");
  const showAllBtn = document.getElementById("show-all");
  const productSearch = document.getElementById("product-search");
  const sortFilter = document.getElementById("sort-filter");
  const tableBody = document.querySelector("#review-table tbody");

  const allRows = Array.from(tableBody.querySelectorAll("tr"));

  applyFilterBtn?.addEventListener("click", () => {
    const searchTerm = productSearch.value.toLowerCase().trim();
    const sortOrder = sortFilter.value;

    let filteredRows = allRows.filter(row => {
      const productName = row.children[0]?.textContent.toLowerCase() || "";
      return productName.includes(searchTerm);
    });

    filteredRows.sort((a, b) => {
      const dateA = parseVietnameseDate(a.children[4]?.textContent || "");
      const dateB = parseVietnameseDate(b.children[4]?.textContent || "");

      if (sortOrder === "latest") {
        return dateB - dateA;
      } else {
        return dateA - dateB;
      }
    });

    tableBody.innerHTML = "";
    filteredRows.forEach(row => tableBody.appendChild(row));
  });

  showAllBtn?.addEventListener("click", () => {
    productSearch.value = "";
    sortFilter.value = "latest";

    tableBody.innerHTML = "";
    allRows.forEach(row => tableBody.appendChild(row));
  });

  function parseVietnameseDate(dateStr) {
    const parts = dateStr.split(" ");
    if (parts.length !== 2) return new Date(0);

    const dateParts = parts[0].split("/");
    const timeParts = parts[1].split(":");

    if (dateParts.length !== 3 || timeParts.length !== 2) return new Date(0);

    const day = parseInt(dateParts[0]);
    const month = parseInt(dateParts[1]) - 1;
    const year = parseInt(dateParts[2]);
    const hour = parseInt(timeParts[0]);
    const minute = parseInt(timeParts[1]);

    return new Date(year, month, day, hour, minute);
  }
});