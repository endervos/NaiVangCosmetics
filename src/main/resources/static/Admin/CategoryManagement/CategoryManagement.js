document.addEventListener("DOMContentLoaded", () => {
  console.log("Category Management loaded!");

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
});

function closePopup() {
  document.getElementById("popupOverlay").style.display = "none";

  document.querySelectorAll(".popup-card").forEach(p => {
    p.style.display = "none";
  });
}

document.getElementById("openAddPopup")?.addEventListener("click", () => {
  document.getElementById("addCategoryName").value = "";
  document.getElementById("addParentCategory").value = "";

  document.getElementById("popupOverlay").style.display = "flex";
  document.getElementById("addPopup").style.display = "block";
});

async function saveNewCategory() {
  const name = document.getElementById("addCategoryName").value.trim();
  const parentId = document.getElementById("addParentCategory").value;

  if (!name) {
    alert("Tên danh mục không được để trống!");
    return;
  }

  if (name.length < 3 || name.length > 150) {
    alert("Tên danh mục phải từ 3-150 ký tự!");
    return;
  }

  const formData = new FormData();
  formData.append("name", name);
  if (parentId) {
    formData.append("parentId", parentId);
  }

  try {
    const response = await fetch("/admin/api/category", {
      method: "POST",
      body: formData
    });

    const result = await response.json();

    if (result.success) {
      alert(result.message);
      closePopup();
      location.reload();
    } else {
      alert(result.message);
    }
  } catch (error) {
    console.error("Error creating category:", error);
    alert("Có lỗi xảy ra khi thêm danh mục!");
  }
}

let editingCategoryId = null;

async function openEditPopup(id) {
  editingCategoryId = id;

  try {
    const response = await fetch(`/admin/api/category/${id}`);
    const category = await response.json();

    document.getElementById("editCategoryName").value = category.name || "";
    document.getElementById("editParentCategory").value = category.parentId || "";

    const parentSelect = document.getElementById("editParentCategory");
    Array.from(parentSelect.options).forEach(option => {
      if (option.value == id) {
        option.disabled = true;
        option.textContent = option.textContent + " (không thể chọn)";
      } else {
        option.disabled = false;
        option.textContent = option.textContent.replace(" (không thể chọn)", "");
      }
    });

    document.getElementById("popupOverlay").style.display = "flex";
    document.getElementById("editPopup").style.display = "block";
  } catch (error) {
    console.error("Error fetching category:", error);
    alert("Không thể tải thông tin danh mục!");
  }
}

async function updateCategory() {
  const name = document.getElementById("editCategoryName").value.trim();
  const parentId = document.getElementById("editParentCategory").value;

  if (!name) {
    alert("Tên danh mục không được để trống!");
    return;
  }

  if (name.length < 3 || name.length > 150) {
    alert("Tên danh mục phải từ 3-150 ký tự!");
    return;
  }

  const formData = new FormData();
  formData.append("name", name);
  if (parentId) {
    formData.append("parentId", parentId);
  }

  try {
    const response = await fetch(`/admin/api/category/${editingCategoryId}`, {
      method: "PUT",
      body: formData
    });

    const result = await response.json();

    if (result.success) {
      alert(result.message);
      closePopup();
      location.reload();
    } else {
      alert(result.message);
    }
  } catch (error) {
    console.error("Error updating category:", error);
    alert("Có lỗi xảy ra khi cập nhật danh mục!");
  }
}

document.getElementById("searchInput")?.addEventListener("input", (e) => {
  const searchTerm = e.target.value.toLowerCase();
  const rows = document.querySelectorAll("#categoryBody tr");

  rows.forEach(row => {
    const name = row.children[1]?.textContent.toLowerCase();
    if (name && name.includes(searchTerm)) {
      row.style.display = "";
    } else {
      row.style.display = "none";
    }
  });
});

window.addEventListener("click", (e) => {
  const overlay = document.getElementById("popupOverlay");
  if (e.target === overlay) {
    closePopup();
  }
});