let currentPage = 1;
const accountsPerPage = 10;
let allAccounts = [];
let filteredAccounts = [];

// DOM elements
const tbody = document.querySelector("#account-table tbody");
const nameSearch = document.querySelector("#name-search");
const emailSearch = document.querySelector("#email-search");
const roleFilter = document.querySelector("#role-filter");
const applyFilterBtn = document.querySelector("#apply-filter");
const showAllBtn = document.querySelector("#show-all");
const prevPageBtn = document.querySelector("#prev-page");
const nextPageBtn = document.querySelector("#next-page");
const pageInfo = document.querySelector("#page-info");
const editAccountModal = document.querySelector("#edit-account-modal");
const deleteConfirmModal = document.querySelector("#delete-confirm-modal");
const closeButtons = document.querySelectorAll(".close-btn");
const editAccountForm = document.querySelector("#edit-account-form");
const confirmDeleteBtn = document.querySelector("#confirm-delete-btn");
const cancelDeleteBtn = document.querySelector("#cancel-delete-btn");

// Format date
function formatDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
}

// Hàm hiển thị toast notification
function showToast(message, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => {
    toast.classList.add('show');
  }, 100);
  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => {
      toast.remove();
    }, 300);
  }, 3000);
}

// Load accounts from API
async function loadAccounts() {
  try {
    const response = await fetch('/admin/api/accounts');
    if (!response.ok) throw new Error('Lỗi khi tải dữ liệu');

    allAccounts = await response.json();
    filteredAccounts = [...allAccounts];
    displayAccounts();
  } catch (error) {
    console.error('Error loading accounts:', error);
    showToast('Không thể tải danh sách tài khoản: ' + error.message, 'error');
  }
}

// Display accounts in table
function displayAccounts() {
  tbody.innerHTML = "";
  const start = (currentPage - 1) * accountsPerPage;
  const end = start + accountsPerPage;
  const paginatedAccounts = filteredAccounts.slice(start, end);

  if (paginatedAccounts.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">Không có dữ liệu</td></tr>';
    pageInfo.textContent = 'Trang 0 / 0';
    prevPageBtn.disabled = true;
    nextPageBtn.disabled = true;
    return;
  }

  paginatedAccounts.forEach(account => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${account.accountId}</td>
      <td>${account.fullname || ''}</td>
      <td>${account.email}</td>
      <td>${formatDate(account.birthday)}</td>
      <td>${account.gender || ''}</td>
      <td>${account.role ? account.role.charAt(0).toUpperCase() + account.role.slice(1) : ''}</td>
      <td class="action-buttons">
        <button class="edit-btn" onclick="editAccount('${account.accountId}')">Sửa</button>
        <button class="delete-btn" onclick="deleteAccount('${account.accountId}')">Xóa</button>
      </td>
    `;
    tbody.appendChild(row);
  });

  // Update pagination info
  const totalPages = Math.ceil(filteredAccounts.length / accountsPerPage);
  pageInfo.textContent = `Trang ${currentPage} / ${totalPages}`;
  prevPageBtn.disabled = currentPage === 1;
  nextPageBtn.disabled = currentPage >= totalPages;
}

// Filter accounts
function filterAccounts() {
  filteredAccounts = [...allAccounts];

  // Filter by name
  const nameTerm = nameSearch.value.trim().toLowerCase();
  if (nameTerm) {
    filteredAccounts = filteredAccounts.filter(account =>
      account.fullname && account.fullname.toLowerCase().includes(nameTerm)
    );
  }

  // Filter by email
  const emailTerm = emailSearch.value.trim().toLowerCase();
  if (emailTerm) {
    filteredAccounts = filteredAccounts.filter(account =>
      account.email.toLowerCase().includes(emailTerm)
    );
  }

  // Filter by role
  const roleValue = roleFilter.value;
  if (roleValue) {
    filteredAccounts = filteredAccounts.filter(account =>
      account.role && account.role.toLowerCase() === roleValue.toLowerCase()
    );
  }

  currentPage = 1; // Reset to first page
  displayAccounts();
}

// Edit account modal
function editAccount(accountId) {
  const account = allAccounts.find(a => a.accountId === accountId);
  if (!account) return;

  // Populate modal
  document.querySelector("#edit-account-id").value = account.accountId;
  document.querySelector("#edit-name").value = account.fullname || '';
  document.querySelector("#edit-email").value = account.email;
  document.querySelector("#edit-phone").value = account.phoneNumber || '';
  document.querySelector("#edit-dob").value = account.birthday || '';

  // Set gender
  const genderSelect = document.querySelector("#edit-gender");
  if (account.gender) {
    Array.from(genderSelect.options).forEach(option => {
      option.selected = option.value === account.gender;
    });
  }

  // Set role
  const roleSelect = document.querySelector("#edit-role");
  if (account.role) {
    Array.from(roleSelect.options).forEach(option => {
      option.selected = option.value.toLowerCase() === account.role.toLowerCase();
    });
  }

  editAccountModal.style.display = "flex";

  // Handle form submit
  editAccountForm.onsubmit = async function(event) {
    event.preventDefault();

    const formData = new FormData();
    formData.append('fullname', document.querySelector("#edit-name").value.trim());
    formData.append('birthday', document.querySelector("#edit-dob").value);
    formData.append('gender', document.querySelector("#edit-gender").value);
    formData.append('phoneNumber', document.querySelector("#edit-phone").value.trim());
    formData.append('role', document.querySelector("#edit-role").value);

    try {
      const response = await fetch(`/admin/api/accounts/${accountId}`, {
        method: 'PUT',
        body: formData
      });

      const data = await response.json();

      if (response.ok && data.success) {
        editAccountModal.style.display = "none";
        await loadAccounts(); // Reload data
        showToast('Cập nhật thông tin tài khoản thành công!', 'success');
      } else {
        showToast(data.message || 'Có lỗi xảy ra', 'error');
      }
    } catch (error) {
      console.error('Error:', error);
      showToast('Có lỗi xảy ra: ' + error.message, 'error');
    }
  };
}

// Delete account
function deleteAccount(accountId) {
  const account = allAccounts.find(a => a.accountId === accountId);
  if (!account) return;

  document.querySelector("#delete-confirm-message").textContent =
    `Bạn có chắc muốn xóa tài khoản ${account.fullname || account.email} (${account.accountId})?`;
  deleteConfirmModal.style.display = "flex";

  confirmDeleteBtn.onclick = async () => {
    try {
      const response = await fetch(`/admin/api/accounts/${accountId}`, {
        method: 'DELETE'
      });

      const data = await response.json();

      if (response.ok && data.success) {
        deleteConfirmModal.style.display = "none";
        await loadAccounts(); // Reload data

        // Adjust current page if needed
        const maxPage = Math.ceil(filteredAccounts.length / accountsPerPage);
        if (currentPage > maxPage && maxPage > 0) {
          currentPage = maxPage;
        }

        displayAccounts();
        showToast('Xóa tài khoản thành công!', 'success');
      } else {
        showToast(data.message || 'Có lỗi xảy ra', 'error');
      }
    } catch (error) {
      console.error('Error:', error);
      showToast('Có lỗi xảy ra: ' + error.message, 'error');
    }
  };

  cancelDeleteBtn.onclick = () => {
    deleteConfirmModal.style.display = "none";
  };
}

// Event listeners
applyFilterBtn.addEventListener("click", filterAccounts);
showAllBtn.addEventListener("click", () => {
  nameSearch.value = "";
  emailSearch.value = "";
  roleFilter.value = "";
  currentPage = 1;
  filteredAccounts = [...allAccounts];
  displayAccounts();
});
prevPageBtn.addEventListener("click", () => {
  if (currentPage > 1) {
    currentPage--;
    displayAccounts();
  }
});
nextPageBtn.addEventListener("click", () => {
  const maxPage = Math.ceil(filteredAccounts.length / accountsPerPage);
  if (currentPage < maxPage) {
    currentPage++;
    displayAccounts();
  }
});
closeButtons.forEach(btn => {
  btn.addEventListener("click", () => {
    btn.closest(".modal").style.display = "none";
  });
});

// Close modals when clicking outside
window.addEventListener("click", (event) => {
  if (event.target.classList.contains("modal")) {
    event.target.style.display = "none";
  }
});

// Initialize
document.addEventListener("DOMContentLoaded", () => {
  editAccountModal.style.display = "none";
  deleteConfirmModal.style.display = "none";
  loadAccounts();
});