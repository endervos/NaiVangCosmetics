// Sample account data (replace with API call in production)
const accounts = [
  { id: "TK001", name: "Nguyễn Văn A", email: "nguyenvana@email.com", dob: "1990-05-15", gender: "Nam", role: "customer" },
  { id: "TK002", name: "Trần Thị B", email: "tranthib@email.com", dob: "1985-12-20", gender: "Nữ", role: "manager" },
  { id: "TK003", name: "Lê Văn C", email: "levanc@email.com", dob: "1995-03-10", gender: "Nam", role: "admin" },
  { id: "TK004", name: "Phạm Thị D", email: "phamthid@email.com", dob: "1992-07-25", gender: "Nữ", role: "customer" },
  { id: "TK005", name: "Hoàng Văn E", email: "hoangvane@email.com", dob: "1988-11-05", gender: "Nam", role: "customer" },
  { id: "TK006", name: "Vũ Thị F", email: "vuthif@email.com", dob: "1993-09-18", gender: "Nữ", role: "manager" },
  { id: "TK007", name: "Đặng Văn G", email: "dangvang@email.com", dob: "1991-04-12", gender: "Nam", role: "customer" },
  { id: "TK008", name: "Bùi Thị H", email: "buithih@email.com", dob: "1987-08-30", gender: "Nữ", role: "manager" },
  { id: "TK009", name: "Ngô Văn I", email: "ngovan@email.com", dob: "1994-02-17", gender: "Nam", role: "admin" },
  { id: "TK010", name: "Lý Thị K", email: "lythik@email.com", dob: "1996-06-22", gender: "Nữ", role: "customer" },
  { id: "TK011", name: "Trần Văn L", email: "tranvanl@email.com", dob: "1990-01-01", gender: "Nam", role: "customer" }
];

// Pagination variables
let currentPage = 1;
const accountsPerPage = 10;
let filteredAccounts = [...accounts]; // Store filtered accounts for pagination

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
const changePasswordModal = document.querySelector("#change-password-modal");
const deleteConfirmModal = document.querySelector("#delete-confirm-modal");
const closeButtons = document.querySelectorAll(".close-btn");
const editAccountForm = document.querySelector("#edit-account-form");
const changePasswordForm = document.querySelector("#change-password-form");
const confirmDeleteBtn = document.querySelector("#confirm-delete-btn");
const cancelDeleteBtn = document.querySelector("#cancel-delete-btn");

// Format date
function formatDate(dateString) {
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

// Display accounts in table
function displayAccounts() {
  tbody.innerHTML = "";
  const start = (currentPage - 1) * accountsPerPage;
  const end = start + accountsPerPage;
  const paginatedAccounts = filteredAccounts.slice(start, end);

  paginatedAccounts.forEach(account => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${account.id}</td>
      <td>${account.name}</td>
      <td>${account.email}</td>
      <td>${formatDate(account.dob)}</td>
      <td>${account.gender}</td>
      <td>${account.role.charAt(0).toUpperCase() + account.role.slice(1)}</td>
      <td class="action-buttons">
        <button class="edit-btn" onclick="editAccount('${account.id}')">Sửa</button>
        <button class="change-password-btn" onclick="changePassword('${account.id}')">Đổi mật khẩu</button>
        <button class="delete-btn" onclick="deleteAccount('${account.id}')">Xóa</button>
      </td>
    `;
    tbody.appendChild(row);
  });

  // Update pagination info
  pageInfo.textContent = `Trang ${currentPage} / ${Math.ceil(filteredAccounts.length / accountsPerPage)}`;
  prevPageBtn.disabled = currentPage === 1;
  nextPageBtn.disabled = currentPage >= Math.ceil(filteredAccounts.length / accountsPerPage);
}

// Filter accounts
function filterAccounts() {
  filteredAccounts = [...accounts];
  
  // Filter by name
  const nameTerm = nameSearch.value.trim().toLowerCase();
  if (nameTerm) {
    filteredAccounts = filteredAccounts.filter(account => 
      account.name.toLowerCase().includes(nameTerm)
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
      account.role === roleValue
    );
  }

  currentPage = 1; // Reset to first page
  displayAccounts();
}

// Edit account modal
function editAccount(accountId) {
  const account = accounts.find(a => a.id === accountId);
  if (!account) return;

  // Populate modal
  document.querySelector("#edit-account-id").value = account.id;
  document.querySelector("#edit-name").value = account.name;
  document.querySelector("#edit-email").value = account.email;
  document.querySelector("#edit-dob").value = account.dob;
  document.querySelector(`#edit-gender option[value="${account.gender}"]`).selected = true;
  document.querySelector(`#edit-role option[value="${account.role}"]`).selected = true;

  editAccountModal.style.display = "flex";

  // Handle form submit
  editAccountForm.onsubmit = function(event) {
    event.preventDefault();

    // Get updated values, exclude email
    const updatedAccount = {
      id: document.querySelector("#edit-account-id").value,
      name: document.querySelector("#edit-name").value.trim(),
      email: account.email, // Keep original email
      dob: document.querySelector("#edit-dob").value,
      gender: document.querySelector("#edit-gender").value,
      role: document.querySelector("#edit-role").value
    };

    // Validate
    if (!updatedAccount.name || !updatedAccount.dob || !updatedAccount.gender || !updatedAccount.role) {
      showToast('Vui lòng điền đầy đủ thông tin!', 'error');
      return;
    }

    // Update account in array
    const index = accounts.findIndex(a => a.id === accountId);
    if (index !== -1) {
      accounts[index] = updatedAccount;
    }

    // Close modal and refresh table
    editAccountModal.style.display = "none";
    filterAccounts();
    showToast('Cập nhật thông tin tài khoản thành công!', 'success');

    // Optional: API call
    /*
    fetch(`/api/accounts/${accountId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      },
      body: JSON.stringify(updatedAccount)
    })
    .then(response => {
      if (!response.ok) throw new Error('Lỗi cập nhật');
      return response.json();
    })
    .then(data => {
      showToast('Cập nhật thông tin tài khoản thành công!', 'success');
    })
    .catch(error => {
      showToast('Có lỗi xảy ra: ' + error.message, 'error');
    });
    */
  };
}

// Change password modal
function changePassword(accountId) {
  const account = accounts.find(a => a.id === accountId);
  if (!account) return;

  // Populate modal
  document.querySelector("#change-password-id").value = account.id;
  document.querySelector("#new-password").value = "";
  document.querySelector("#confirm-password").value = "";

  changePasswordModal.style.display = "flex";

  // Handle form submit
  changePasswordForm.onsubmit = function(event) {
    event.preventDefault();

    // Get new password and confirmation
    const newPassword = document.querySelector("#new-password").value.trim();
    const confirmPassword = document.querySelector("#confirm-password").value.trim();

    // Validate
    if (!newPassword || newPassword.length < 6) {
      showToast('Mật khẩu mới phải có ít nhất 6 ký tự!', 'error');
      return;
    }
    if (newPassword !== confirmPassword) {
      showToast('Mật khẩu xác nhận không khớp!', 'error');
      return;
    }

    // Simulate password update (store in array or send to API)
    console.log(`Cập nhật mật khẩu cho tài khoản ${accountId}: ${newPassword}`);

    // Close modal
    changePasswordModal.style.display = "none";
    showToast(`Đổi mật khẩu cho tài khoản ${accountId} thành công!`, 'success');

    // Optional: API call
    /*
    fetch(`/api/accounts/${accountId}/change-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      },
      body: JSON.stringify({ password: newPassword })
    })
    .then(response => {
      if (!response.ok) throw new Error('Lỗi đổi mật khẩu');
      return response.json();
    })
    .then(data => {
      showToast(`Đổi mật khẩu cho tài khoản ${accountId} thành công!`, 'success');
    })
    .catch(error => {
      showToast('Có lỗi xảy ra: ' + error.message, 'error');
    });
    */
  };
}

// Delete account
function deleteAccount(accountId) {
  const account = accounts.find(a => a.id === accountId);
  if (!account) return;

  // Populate modal
  document.querySelector("#delete-confirm-message").textContent = 
    `Bạn có chắc muốn xóa tài khoản ${account.name} (${account.id})?`;
  deleteConfirmModal.style.display = "flex";

  // Handle confirm delete
  confirmDeleteBtn.onclick = () => {
    const index = accounts.findIndex(a => a.id === accountId);
    if (index !== -1) {
      accounts.splice(index, 1);
    }
    filterAccounts();
    const maxPage = Math.ceil(filteredAccounts.length / accountsPerPage);
    if (currentPage > maxPage && maxPage > 0) {
      currentPage = maxPage;
    }
    displayAccounts();
    showToast(`Xóa tài khoản ${accountId} thành công!`, 'success');
    deleteConfirmModal.style.display = "none";

    // Optional: API call
    /*
    fetch(`/api/accounts/${accountId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      }
    })
    .then(response => {
      if (!response.ok) throw new Error('Lỗi xóa tài khoản');
      showToast(`Xóa tài khoản ${accountId} thành công!`, 'success');
    })
    .catch(error => {
      showToast('Có lỗi xảy ra: ' + error.message, 'error');
    });
    */
  };

  // Handle cancel
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
  filteredAccounts = [...accounts];
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
  changePasswordModal.style.display = "none";
  deleteConfirmModal.style.display = "none";
  displayAccounts();
});