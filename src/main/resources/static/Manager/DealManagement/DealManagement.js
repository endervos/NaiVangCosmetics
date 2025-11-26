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

  /* ============================================================
   JS BỔ SUNG CHO DEAL MANAGER – KHÔNG SỬA CODE GỐC
============================================================ */

    console.log("Deal Manager functions loaded!");

    // Fake data ban đầu (có thể thay bằng API sau này)
    let deals = [
    {
        id: 1,
        title: "Giảm 10% skincare",
        discount: 10,
        startDate: "2025-11-01",
        endDate: "2025-11-20",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 2,
        title: "Ưu đãi 15% dầu gội",
        discount: 15,
        startDate: "2025-11-05",
        endDate: "2025-11-15",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 3,
        title: "Giảm 10% skincare",
        discount: 10,
        startDate: "2025-11-01",
        endDate: "2025-11-20",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 4,
        title: "Ưu đãi 15% dầu gội",
        discount: 15,
        startDate: "2025-11-05",
        endDate: "2025-11-15",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 5,
        title: "Giảm 10% skincare",
        discount: 10,
        startDate: "2025-11-01",
        endDate: "2025-11-20",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 6,
        title: "Ưu đãi 15% dầu gội",
        discount: 15,
        startDate: "2025-11-05",
        endDate: "2025-11-15",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 7,
        title: "Giảm 10% skincare",
        discount: 10,
        startDate: "2025-11-01",
        endDate: "2025-11-20",
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 8,
        title: "Ưu đãi 15% dầu gội",
        discount: 15,
        startDate: "2025-11-05",
        endDate: "2025-11-15",
        image: "./Image/suaruamat.jpg"
    }
    ];

    // Lấy DOM
    const dealList = document.getElementById("deal-list");
    const modal = document.getElementById("deal-modal");
    const closeModal = document.querySelector(".close-modal");
    const addBtn = document.querySelector(".add-btn");
    const form = document.getElementById("deal-form");
    const modalTitle = document.getElementById("modal-title");

    let editingId = null;

    /* ============================================================
    RENDER BẢNG
    ============================================================ */
    function getStatus(start, end) {
    const today = new Date();
    const s = new Date(start);
    const e = new Date(end);

    if (today >= s && today <= e) {
        return `<span class="status-active">Đang áp dụng</span>`;
    }
    return `<span class="status-expired">Hết hạn</span>`;
    }

    function renderDeals() {
    dealList.innerHTML = "";

    deals.forEach(d => {
        const row = document.createElement("tr");

        row.innerHTML = `
        <td>${d.id}</td>
        <td style="text-align:left">${d.title}</td>
        <td>${d.discount}%</td>
        <td>${d.startDate}</td>
        <td>${d.endDate}</td>
        <td><img src="${d.image}" class="deal-img"></td>
        <td>${getStatus(d.startDate, d.endDate)}</td>
        <td>
            <button class="edit-btn" onclick="editDeal(${d.id})">Sửa</button>
        </td>
        `;

        dealList.appendChild(row);
    });
    }

    renderDeals();

    /* ============================================================
    MỞ MODAL – chế độ thêm mới
    ============================================================ */
    addBtn?.addEventListener("click", () => {
    editingId = null;
    modalTitle.textContent = "Tạo khuyến mãi";
    form.reset();
    modal.classList.add("show");
    });

    /* ĐÓNG MODAL */
    closeModal?.addEventListener("click", () => {
    modal.classList.remove("show");
    });

    window.addEventListener("click", e => {
    if (e.target === modal) modal.classList.remove("show");
    });

    /* ============================================================
    EDIT – mở modal với dữ liệu
    ============================================================ */
    window.editDeal = id => {
    const d = deals.find(x => x.id === id);
    if (!d) return;

    editingId = id;

    modalTitle.textContent = "Chỉnh sửa khuyến mãi";
    document.getElementById("title").value = d.title;
    document.getElementById("discount").value = d.discount;
    document.getElementById("startDate").value = d.startDate;
    document.getElementById("endDate").value = d.endDate;

    modal.classList.add("show");
    };

    /* ============================================================
    SUBMIT FORM – thêm hoặc sửa
    ============================================================ */
    form?.addEventListener("submit", e => {
    e.preventDefault();

    const title = document.getElementById("title").value.trim();
    const discount = document.getElementById("discount").value;
    const start = document.getElementById("startDate").value;
    const end = document.getElementById("endDate").value;

    let imagePreview = "./Image/default.png";
    const imageFile = document.getElementById("image").files[0];

    if (imageFile) {
        imagePreview = URL.createObjectURL(imageFile);
    }

    // THÊM
    if (editingId === null) {
        deals.push({
        id: Date.now(),
        title,
        discount,
        startDate: start,
        endDate: end,
        image: imagePreview
        });
    }
    // SỬA
    else {
        const idx = deals.findIndex(x => x.id === editingId);
        deals[idx].title = title;
        deals[idx].discount = discount;
        deals[idx].startDate = start;
        deals[idx].endDate = end;
        if (imageFile) deals[idx].image = imagePreview;
    }

    modal.classList.remove("show");
    renderDeals();
    });

});
