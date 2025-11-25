document.addEventListener("DOMContentLoaded", () => {
  console.log("Admin page loaded!");

  // Toggle dropdown menu
  const userInfo = document.querySelector(".user-info");
  const dropdown = document.querySelector(".dropdown");

  if (userInfo && dropdown) {
    userInfo.addEventListener("click", () => {
      dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
    });

    // Đóng dropdown khi click ra ngoài
    document.addEventListener("click", (e) => {
      if (!userInfo.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.style.display = "none";
      }
    });
  }

  // Logout icon ngoài (chỉ dùng nếu bạn giữ icon riêng ngoài top-bar)
  const logoutBtn = document.querySelector(".logout-icon");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      if (confirm("Bạn có chắc chắn muốn đăng xuất không?")) {
        window.location.href = "/src/main/resources/templates/Customer/Login.html";
      }
    });
  }

   /* ====================================
     DỮ LIỆU DEMO KHUYẾN MÃI
  ===================================== */
  let promotions = [
    {
      id: 1,
      title: "Giảm 20% sản phẩm phấn má",
      discount: 20,
      startDate: "2025-11-14",
      endDate: "2025-11-16",
      image: "./Image/phanma.jpg",
      status: "active"
    },
    {
      id: 2,
      title: "Giảm 15% sản phẩm sữa rửa mặt",
      discount: 15,
      startDate: "2025-11-05",
      endDate: "2025-11-10",
      image: "./Image/suaruamat.jpg",
      status: "expired"
    },
    {
      id: 3,
      title: "Giảm 20% sản phẩm phấn má",
      discount: 20,
      startDate: "2025-11-14",
      endDate: "2025-11-16",
      image: "./Image/phanma.jpg",
      status: "active"
    },
    {
      id: 4,
      title: "Giảm 15% sản phẩm sữa rửa mặt",
      discount: 15,
      startDate: "2025-11-05",
      endDate: "2025-11-10",
      image: "./Image/suaruamat.jpg",
      status: "expired"
    },
    {
      id: 5,
      title: "Giảm 20% sản phẩm phấn má",
      discount: 20,
      startDate: "2025-11-14",
      endDate: "2025-11-16",
      image: "./Image/phanma.jpg",
      status: "active"
    },
    {
      id: 6,
      title: "Giảm 15% sản phẩm sữa rửa mặt",
      discount: 15,
      startDate: "2025-11-05",
      endDate: "2025-11-10",
      image: "./Image/suaruamat.jpg",
      status: "expired"
    }
  ];

  const dealTable = document.getElementById("deal-list");

  function renderTable() {
    dealTable.innerHTML = "";

    promotions.forEach((deal) => {
      const tr = document.createElement("tr");

      tr.innerHTML = `
        <td>${deal.id}</td>
        <td>${deal.title}</td>
        <td>${deal.discount}%</td>
        <td>${deal.startDate}</td>
        <td>${deal.endDate}</td>
        <td><img src="${deal.image}" class="deal-img"></td>
        <td>
          <span class="${deal.status === 'active' ? 'status-active' : 'status-expired'}">
            ${deal.status === "active" ? "Đang áp dụng" : "Hết hạn"}
          </span>
        </td>
        <td>
          <button class="action-btn edit-btn" data-id="${deal.id}">Sửa</button>
          <button class="action-btn delete-btn" data-id="${deal.id}">Xóa</button>
        </td>
      `;
      dealTable.appendChild(tr);
    });
  }

  renderTable();


  /* ====================================
     MODAL THÊM / SỬA
  ===================================== */
  const modal = document.getElementById("deal-modal");
  const closeModalBtn = document.querySelector(".close-modal");
  const addBtn = document.querySelector(".add-btn");
  const dealForm = document.getElementById("deal-form");

  let editMode = false;
  let editId = null;

  // Mở modal thêm
addBtn.addEventListener("click", () => {
  editMode = false;
  editId = null;
  document.getElementById("modal-title").textContent = "Thêm khuyến mãi";
  dealForm.reset();
  modal.classList.add("show");
});

// Đóng modal
closeModalBtn.addEventListener("click", () => modal.classList.remove("show"));
window.addEventListener("click", (e) => {
  if (e.target === modal) modal.classList.remove("show");
});


  /* ====================================
     CLICK NÚT SỬA & XOÁ
  ===================================== */
  document.addEventListener("click", (e) => {
    // XOÁ
    if (e.target.classList.contains("delete-btn")) {
      const id = +e.target.dataset.id;
      if (confirm("Bạn có chắc muốn xoá khuyến mãi này?")) {
        promotions = promotions.filter((d) => d.id !== id);
        renderTable();
      }
    }

    // SỬA
    if (e.target.classList.contains("edit-btn")) {
      const id = +e.target.dataset.id;
      const deal = promotions.find((d) => d.id === id);

      if (deal) {
        editMode = true;
        editId = id;
        document.getElementById("modal-title").textContent = "Chỉnh sửa khuyến mãi";

        document.getElementById("title").value = deal.title;
        document.getElementById("description").value = deal.description || "";
        document.getElementById("discount").value = deal.discount;
        document.getElementById("startDate").value = deal.startDate;
        document.getElementById("endDate").value = deal.endDate;

        modal.classList.add("show");
;
      }
    }
  });


  /* ====================================
     XỬ LÝ SUBMIT FORM
  ===================================== */
  dealForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const newDeal = {
      id: editMode ? editId : promotions.length + 1,
      title: document.getElementById("title").value,
      description: document.getElementById("description").value,
      discount: document.getElementById("discount").value,
      startDate: document.getElementById("startDate").value,
      endDate: document.getElementById("endDate").value,
      image: "./Image/default.jpg",
      status: "active"
    };

    if (editMode) {
      const index = promotions.findIndex((d) => d.id === editId);
      promotions[index] = newDeal;
    } else {
      promotions.push(newDeal);
    }

    renderTable();
    modal.classList.remove("show");
  });

});

