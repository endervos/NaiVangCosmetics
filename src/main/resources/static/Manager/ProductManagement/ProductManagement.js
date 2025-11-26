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
    JS BỔ SUNG – PRODUCT MANAGER (KHÔNG SỬA CODE CŨ)
    ============================================================ */

    console.log("Product Management loaded!");

    // Danh sách sản phẩm mẫu (sau này thay API)
    let products = [
    {
        id: 1,
        name: "Sữa rửa mặt dịu nhẹ",
        price: 129000,
        category: "Skincare",
        status: "Còn hàng",
        quantity: 50,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 2,
        name: "Dầu gội phục hồi",
        price: 89000,
        category: "Haircare",
        status: "Hết hàng",
        quantity: 0,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 3,
        name: "Sữa rửa mặt dịu nhẹ",
        price: 129000,
        category: "Skincare",
        status: "Còn hàng",
        quantity: 50,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 4,
        name: "Dầu gội phục hồi",
        price: 89000,
        category: "Haircare",
        status: "Hết hàng",
        quantity: 0,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 5,
        name: "Sữa rửa mặt dịu nhẹ",
        price: 129000,
        category: "Skincare",
        status: "Còn hàng",
        quantity: 10,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 6,
        name: "Dầu gội phục hồi",
        price: 89000,
        category: "Haircare",
        status: "Hết hàng",
        quantity: 0,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 7,
        name: "Sữa rửa mặt dịu nhẹ",
        price: 129000,
        category: "Skincare",
        status: "Còn hàng",
        quantity: 10,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 8,
        name: "Dầu gội phục hồi",
        price: 89000,
        category: "Haircare",
        status: "Hết hàng",
        quantity: 0,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 9,
        name: "Sữa rửa mặt dịu nhẹ",
        price: 129000,
        category: "Skincare",
        status: "Còn hàng",
        quantity: 10,
        image: "./Image/suaruamat.jpg"
    },
    {
        id: 10,
        name: "Dầu gội phục hồi",
        price: 89000,
        category: "Haircare",
        status: "Hết hàng",
        quantity: 0,
        image: "./Image/suaruamat.jpg"
    }
    ];

    // DOM
    const productList = document.getElementById("product-list");
    const modal = document.getElementById("product-modal");
    const closeModal = document.querySelector(".close-modal");
    const addBtn = document.querySelector(".add-product-btn");
    const form = document.getElementById("product-form");
    const modalTitle = document.getElementById("modal-title");

    let editingId = null;

    /* ============================================================
    RENDER BẢNG SẢN PHẨM
    ============================================================ */
    function renderProducts() {
    productList.innerHTML = "";

    products.forEach(p => {
        const row = document.createElement("tr");

        row.innerHTML = `
        <td>${p.id}</td>
        <td><img src="${p.image}" class="product-img"></td>
        <td style="text-align:left">${p.name}</td>
        <td>${p.price.toLocaleString("vi-VN")} đ</td>
        <td>${p.quantity}</td>
        <td>${p.category}</td>
        <td>
            ${
            p.status === "Còn hàng"
                ? '<span class="status-instock">Còn hàng</span>'
                : '<span class="status-outstock">Hết hàng</span>'
            }
        </td>
        <td>
            <button class="edit-btn" onclick="editProduct(${p.id})">Sửa</button>
        </td>
        `;

        productList.appendChild(row);
    });
    }

    renderProducts();

    /* ============================================================
    MỞ MODAL – THÊM MỚI
    ============================================================ */
    addBtn?.addEventListener("click", () => {
    editingId = null;
    modalTitle.textContent = "Thêm sản phẩm";
    form.reset();
    modal.classList.add("show");
    });

    /* ĐÓNG MODAL */
    closeModal?.addEventListener("click", () => {
    modal.classList.remove("show");
    });

    window.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.remove("show");
    });

    /* ============================================================
    CHẾ ĐỘ EDIT – ĐƯA DỮ LIỆU LÊN FORM
    ============================================================ */
    window.editProduct = (id) => {
    const p = products.find(x => x.id === id);
    if (!p) return;

    editingId = id;

    modalTitle.textContent = "Chỉnh sửa sản phẩm";

    document.getElementById("product-name").value = p.name;
    document.getElementById("product-price").value = p.price;
    document.getElementById("product-category").value = p.category;
    document.getElementById("product-status").value = p.status;
    document.getElementById("product-quantity").value = p.quantity;

    modal.classList.add("show");
    };

    /* ============================================================
    SUBMIT FORM – THÊM hoặc SỬA
    ============================================================ */
    form?.addEventListener("submit", (e) => {
    e.preventDefault();

    const name = document.getElementById("product-name").value.trim();
    const price = Number(document.getElementById("product-price").value);
    const category = document.getElementById("product-category").value;
    const status = document.getElementById("product-status").value;
    const quantity = Number(document.getElementById("product-quantity").value);

    let imgPreview = "./Image/default.png";
    const file = document.getElementById("product-image").files[0];

    if (file) {
        imgPreview = URL.createObjectURL(file);
    }

    // ===== THÊM MỚI =====
    if (editingId === null) {
        products.push({
        id: Date.now(),
        name,
        price,
        category,
        status,
        quantity,
        image: imgPreview
        });
    }
    // ===== CẬP NHẬT =====
    else {
        const index = products.findIndex(p => p.id === editingId);

        products[index].name = name;
        products[index].price = price;
        products[index].category = category;
        products[index].status = status;
        products[index].quantity = quantity;

        if (file) products[index].image = imgPreview;
    }

    modal.classList.remove("show");
    renderProducts();
    });



});