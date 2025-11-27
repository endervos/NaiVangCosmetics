document.addEventListener("DOMContentLoaded", () => {
  console.log("Product Management loaded!");

  const userInfo = document.querySelector(".user-info");
  const dropdown = document.querySelector(".dropdown");

  if (userInfo && dropdown) {
    userInfo.addEventListener("click", () => {
      dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
    });

    document.addEventListener("click", (e) => {
      if (!userInfo.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.style.display = "none";
      }
    });
  }

  const modal = document.getElementById("product-modal");
  const viewModal = document.getElementById("view-modal");
  const closeModal = document.querySelector(".close-modal");
  const closeViewModal = document.getElementById("close-view-modal");
  const addBtn = document.querySelector(".add-product-btn");
  const form = document.getElementById("product-form");
  const modalTitle = document.getElementById("modal-title");

  let editingId = null;

  addBtn?.addEventListener("click", () => {
    editingId = null;
    modalTitle.textContent = "Thêm sản phẩm";
    form.reset();
    document.getElementById("current-image-preview").style.display = "none";
    modal.classList.add("show");
  });

  closeModal?.addEventListener("click", () => {
    modal.classList.remove("show");
  });

  closeViewModal?.addEventListener("click", () => {
    viewModal.classList.remove("show");
  });

  window.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.remove("show");
    if (e.target === viewModal) viewModal.classList.remove("show");
  });

  window.viewProduct = async (id) => {
    try {
      console.log("Fetching product with ID:", id);

      const response = await fetch(`/item/api/${id}`);

      console.log("Response status:", response.status);

      if (!response.ok) {
        const errorText = await response.text();
        console.error("Error response:", errorText);
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const item = await response.json();
      console.log("Received item:", item);

      document.getElementById("view-product-id").textContent = item.itemId || "-";
      document.getElementById("view-product-name").textContent = item.name || "-";
      document.getElementById("view-product-price").textContent =
        item.price ? new Intl.NumberFormat('vi-VN').format(item.price) + " đ" : "-";
      document.getElementById("view-product-category").textContent = item.category?.name || "-";
      document.getElementById("view-product-color").textContent = item.color || "-";
      document.getElementById("view-product-ingredient").textContent = item.ingredient || "-";
      document.getElementById("view-product-description").textContent = item.description || "-";

      if (item.createdAt) {
        const createdDate = new Date(item.createdAt);
        document.getElementById("view-product-created").textContent =
          createdDate.toLocaleDateString('vi-VN');
      } else {
        document.getElementById("view-product-created").textContent = "-";
      }

      if (item.updatedAt) {
        const updatedDate = new Date(item.updatedAt);
        document.getElementById("view-product-updated").textContent =
          updatedDate.toLocaleDateString('vi-VN');
      } else {
        document.getElementById("view-product-updated").textContent = "-";
      }

      if (item.images && item.images.length > 0) {
        document.getElementById("view-product-image").src =
          `/api/item-images/blob/${item.images[0].itemImageId}`;
      } else {
        document.getElementById("view-product-image").src =
          "/Manager/ProductManagement/Image/default.png";
      }

      viewModal.classList.add("show");
    } catch (error) {
      console.error("Detailed error:", error);
      alert("Không thể tải thông tin sản phẩm! Chi tiết: " + error.message);
    }
  };

  window.editProduct = async (id) => {
    try {
      const response = await fetch(`/item/api/${id}`);
      const item = await response.json();

      editingId = id;
      modalTitle.textContent = "Chỉnh sửa sản phẩm";

      document.getElementById("product-id").value = item.itemId;
      document.getElementById("product-name").value = item.name || "";
      document.getElementById("product-description").value = item.description || "";
      document.getElementById("product-color").value = item.color || "";
      document.getElementById("product-ingredient").value = item.ingredient || "";
      document.getElementById("product-price").value = item.price || "";
      document.getElementById("product-category").value = item.category?.categoryId || "";

      if (item.images && item.images.length > 0) {
        document.getElementById("preview-img").src = `/api/item-images/blob/${item.images[0].itemImageId}`;
        document.getElementById("current-image-preview").style.display = "block";
      } else {
        document.getElementById("current-image-preview").style.display = "none";
      }

      modal.classList.add("show");
    } catch (error) {
      console.error("Error fetching item:", error);
      alert("Không thể tải thông tin sản phẩm!");
    }
  };

  form?.addEventListener("submit", async (e) => {
    e.preventDefault();

    const itemId = document.getElementById("product-id").value;
    const name = document.getElementById("product-name").value.trim();
    const description = document.getElementById("product-description").value.trim();
    const color = document.getElementById("product-color").value.trim();
    const ingredient = document.getElementById("product-ingredient").value.trim();
    const price = Number(document.getElementById("product-price").value);
    const categoryId = document.getElementById("product-category").value;
    const imageFile = document.getElementById("product-image").files[0];

    if (!name || !description || !color || !ingredient || !price || !categoryId) {
      alert("Vui lòng điền đầy đủ thông tin!");
      return;
    }

    const formData = new FormData();
    formData.append("name", name);
    formData.append("description", description);
    formData.append("color", color);
    formData.append("ingredient", ingredient);
    formData.append("price", price);
    formData.append("categoryId", categoryId);

    if (imageFile) {
      formData.append("image", imageFile);
    }

    try {
      let response;

      if (editingId !== null && itemId) {
        response = await fetch(`/item/api/${itemId}`, {
          method: "PUT",
          body: formData
        });
      }
      else {
        response = await fetch(`/item/api`, {
          method: "POST",
          body: formData
        });
      }

      const result = await response.json();

      if (result.success) {
        alert(result.message);
        modal.classList.remove("show");
        location.reload();
      } else {
        alert(result.message);
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Có lỗi xảy ra khi lưu sản phẩm!");
    }
  });

});