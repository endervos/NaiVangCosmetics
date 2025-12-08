document.addEventListener("DOMContentLoaded", () => {
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
  const customerSearch = document.getElementById("customer-search");
  const sortFilter = document.getElementById("sort-filter");
  const tableBody = document.querySelector("#review-table tbody");
  const allRows = Array.from(tableBody.querySelectorAll("tr"));

  function sanitizeInput(input) {
    return input.replace(/[!@#$%^&*()+=\[\]{}|;:'",.<>?/\\`~_\-]/g, '');
  }

  productSearch.addEventListener('input', (e) => {
    const sanitized = sanitizeInput(e.target.value);
    if (e.target.value !== sanitized) {
      e.target.value = sanitized;
    }
  });

  customerSearch.addEventListener('input', (e) => {
    const sanitized = sanitizeInput(e.target.value);
    if (e.target.value !== sanitized) {
      e.target.value = sanitized;
    }
  });

  productSearch.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      applyFilters();
    }
  });

  customerSearch.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      applyFilters();
    }
  });

  function applyFilters() {
    const productSearchTerm = sanitizeInput(productSearch.value.toLowerCase().trim());
    const customerSearchTerm = sanitizeInput(customerSearch.value.toLowerCase().trim());
    const sortOrder = sortFilter.value;
    let filteredRows = allRows.filter(row => {
      const productName = row.children[0]?.textContent.toLowerCase() || "";
      const customerName = row.children[1]?.textContent.toLowerCase() || "";
      let matchProduct = true;
      let matchCustomer = true;
      if (productSearchTerm) {
        matchProduct = productName.includes(productSearchTerm);
      }
      if (customerSearchTerm) {
        matchCustomer = customerName.includes(customerSearchTerm);
      }
      return matchProduct && matchCustomer;
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
    if (filteredRows.length === 0) {
      tableBody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Không tìm thấy đánh giá nào</td></tr>';
    } else {
      filteredRows.forEach(row => tableBody.appendChild(row));
    }
  }

  applyFilterBtn?.addEventListener("click", applyFilters);

  showAllBtn?.addEventListener("click", () => {
    productSearch.value = "";
    customerSearch.value = "";
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