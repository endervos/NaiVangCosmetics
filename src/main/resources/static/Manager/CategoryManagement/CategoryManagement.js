/* =========================================================
   DROPDOWN USER + LOGOUT
========================================================= */

document.addEventListener("DOMContentLoaded", () => {
  console.log("Admin page loaded!");

  const userInfo = document.querySelector(".user-info");
  const dropdown = document.querySelector(".dropdown");

  if (userInfo && dropdown) {
    userInfo.addEventListener("click", (e) => {
      e.stopPropagation();
      dropdown.style.display =
        dropdown.style.display === "block" ? "none" : "block";
    });

    // Đóng dropdown khi click ra ngoài
    document.addEventListener("click", (e) => {
      if (!userInfo.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.style.display = "none";
      }
    });
  }

  // Logout icon ngoài (nếu có icon riêng)
  const logoutIcon = document.querySelector(".logout-icon");
  if (logoutIcon) {
    logoutIcon.addEventListener("click", () => {
      if (confirm("Bạn có chắc chắn muốn đăng xuất không?")) {
        window.location.href = "/src/main/resources/templates/Customer/Login.html";
      }
    });
  }

  // Logout trong dropdown
  const logoutBtn = document.querySelector(".logout-btn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault();
      if (confirm("Bạn muốn đăng xuất?")) {
        window.location.href = "/src/main/resources/templates/Customer/Login.html";
      }
    });
  }

  // Sau khi load dropdown → render bảng danh mục
  renderTable();
});


/* =========================================================
   DỮ LIỆU MẪU DANH MỤC
========================================================= */

let categories = [
  { id: 1, name: "Chăm sóc da mặt", status: "active", totalProducts: 12 },
  { id: 2, name: "Trang điểm", status: "active", totalProducts: 8 },
  { id: 3, name: "Chăm sóc tóc", status: "inactive", totalProducts: 4 },
  { id: 4, name: "Chăm sóc da mặt", status: "active", totalProducts: 12 },
  { id: 5, name: "Trang điểm", status: "active", totalProducts: 8 },
  { id: 6, name: "Chăm sóc tóc", status: "inactive", totalProducts: 4 },
  { id: 7, name: "Chăm sóc da mặt", status: "active", totalProducts: 12 },
  { id: 8, name: "Trang điểm", status: "active", totalProducts: 8 },
  { id: 9, name: "Chăm sóc tóc", status: "inactive", totalProducts: 4 },
  { id: 10, name: "Chăm sóc da mặt", status: "active", totalProducts: 12 },
  { id: 11, name: "Trang điểm", status: "active", totalProducts: 8 },
  { id: 12, name: "Chăm sóc tóc", status: "inactive", totalProducts: 4 },
  { id: 13, name: "Chăm sóc da mặt", status: "active", totalProducts: 12 },
  { id: 14, name: "Trang điểm", status: "active", totalProducts: 8 },
  { id: 15, name: "Chăm sóc tóc", status: "inactive", totalProducts: 4 },
  { id: 16, name: "Chăm sóc da mặt", status: "active", totalProducts: 12 },
  { id: 17, name: "Trang điểm", status: "active", totalProducts: 8 },
  { id: 18, name: "Chăm sóc tóc", status: "inactive", totalProducts: 4 },
];


// Pagination settings
let itemsPerPage = 10;
let currentPage = 1;



/* =========================================================
   RENDER BẢNG DANH MỤC
========================================================= */

function renderTable() {
  const body = document.getElementById("categoryBody");
  body.innerHTML = "";

  const start = (currentPage - 1) * itemsPerPage;
  const end = start + itemsPerPage;

  const list = categories.slice(start, end);

  list.forEach(cat => {
    body.innerHTML += `
      <tr>
        <td>${cat.id}</td>
        <td>${cat.name}</td>

        <td>
          <span class="status ${cat.status}">
            ${cat.status === "active" ? "Hoạt động" : "Tạm ẩn"}
          </span>
        </td>

        <td class="count-col">${cat.totalProducts ?? 0}</td>

        <td>
          <button class="edit-btn" onclick="openEditPopup(${cat.id})">
            <i class="fa-solid fa-pen"></i>
          </button>

        </td>
      </tr>
    `;
  });

  renderPagination();
}


function renderPagination() {
  const totalPages = Math.ceil(categories.length / itemsPerPage);
  const pag = document.getElementById("pagination");

  pag.innerHTML = "";

  for (let i = 1; i <= totalPages; i++) {
    pag.innerHTML += `
      <button
        class="${i === currentPage ? 'active-page' : ''}"
        onclick="changePage(${i})"
      >${i}</button>
    `;
  }
}

function changePage(page) {
  currentPage = page;
  renderTable();
}



/* =========================================================
   POPUP (MỞ – ĐÓNG)
========================================================= */

function closePopup() {
  document.getElementById("popupOverlay").style.display = "none";

  document.querySelectorAll(".popup-card").forEach(p => {
    p.style.display = "none";
  });
}

// Mở popup thêm
document.getElementById("openAddPopup").addEventListener("click", () => {
  document.getElementById("popupOverlay").style.display = "flex";
  document.getElementById("addPopup").style.display = "block";
});


/* =========================================================
   THÊM DANH MỤC
========================================================= */

function saveNewCategory() {
  const name = document.getElementById("addCategoryName").value.trim();
  const status = document.getElementById("addCategoryStatus").value;

  if (!name) {
    alert("Tên danh mục không được để trống!");
    return;
  }

  categories.push({
    id: categories.length + 1,
    name,
    status
  });

  closePopup();
  renderTable();
}


/* =========================================================
   SỬA DANH MỤC
========================================================= */

function openEditPopup(id) {
  editId = id;
  const cat = categories.find(c => c.id === id);

  document.getElementById("editCategoryName").value = cat.name;
  document.getElementById("editCategoryStatus").value = cat.status;

  document.getElementById("popupOverlay").style.display = "flex";
  document.getElementById("editPopup").style.display = "block";
}

function updateCategory() {
  const name = document.getElementById("editCategoryName").value.trim();
  const status = document.getElementById("editCategoryStatus").value;

  const index = categories.findIndex(c => c.id === editId);

  categories[index].name = name;
  categories[index].status = status;

  closePopup();
  renderTable();
}

