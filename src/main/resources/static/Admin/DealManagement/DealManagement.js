document.addEventListener("DOMContentLoaded", () => {
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

    const dealList = document.getElementById("deal-list");
    const modal = document.getElementById("deal-modal");
    const closeModal = document.querySelector(".close-modal");
    const addBtn = document.querySelector(".add-btn");
    const form = document.getElementById("deal-form");
    const modalTitle = document.getElementById("modal-title");
    const voucherCodeSearch = document.getElementById("voucher-code-search");
    const startDateFilter = document.getElementById("start-date-filter");
    const endDateFilter = document.getElementById("end-date-filter");
    const applyFilterBtn = document.getElementById("apply-filter-btn");
    const showAllBtn = document.getElementById("show-all-btn");
    let editingId = null;
    let allVouchers = [];

    function sanitizeInput(input) {
        return input.replace(/[!@#$%^&*()+=\[\]{}|;:'"<>?/\\`~]/g, '');
    }

    voucherCodeSearch.addEventListener('input', (e) => {
        const sanitized = sanitizeInput(e.target.value);
        if (e.target.value !== sanitized) {
            e.target.value = sanitized;
        }
    });

    voucherCodeSearch.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            filterVouchers();
        }
    });

    async function loadVouchers() {
        try {
            const response = await fetch('/admin/api/vouchers');
            allVouchers = await response.json();
            renderVouchers(allVouchers);
        } catch (error) {
            console.error('Lỗi khi tải voucher:', error);
            alert('Không thể tải danh sách voucher');
        }
    }

    function renderVouchers(vouchers) {
        dealList.innerHTML = "";
        if (vouchers.length === 0) {
            dealList.innerHTML = '<tr><td colspan="10" style="text-align: center;">Không có voucher nào</td></tr>';
            return;
        }
        vouchers.forEach(v => {
            const row = document.createElement("tr");
            const status = getStatus(v.startDate, v.endDate, v.isActive);
            row.setAttribute('data-voucher-code', v.code);
            row.setAttribute('data-start-date', v.startDate);
            row.setAttribute('data-end-date', v.endDate);
            row.innerHTML = `
                <td>${v.voucherId}</td>
                <td style="text-align:left">${v.code}</td>
                <td style="text-align:left">${v.description || ''}</td>
                <td>${v.discountPercent}%</td>
                <td>${v.maxUses || 'Không giới hạn'}</td>
                <td>${v.usedCount || 0}</td>
                <td>${formatDateTime(v.startDate)}</td>
                <td>${formatDateTime(v.endDate)}</td>
                <td>${status}</td>
                <td>
                    <button class="edit-btn" onclick="editVoucher(${v.voucherId})">Sửa</button>
                    <button class="delete-btn" onclick="deleteVoucher(${v.voucherId})">Xóa</button>
                </td>
            `;
            dealList.appendChild(row);
        });
    }

    function filterVouchers() {
        const searchCode = sanitizeInput(voucherCodeSearch.value.trim().toLowerCase());
        const startDateValue = startDateFilter.value;
        const endDateValue = endDateFilter.value;
        const allRows = dealList.querySelectorAll('tr');
        allRows.forEach(row => {
            const voucherCode = row.getAttribute('data-voucher-code')?.toLowerCase() || '';
            const voucherStartDate = row.getAttribute('data-start-date') || '';
            const voucherEndDate = row.getAttribute('data-end-date') || '';
            let matchCode = true;
            let matchStartDate = true;
            let matchEndDate = true;
            if (searchCode) {
                matchCode = voucherCode.includes(searchCode);
            }
            if (startDateValue) {
                const filterStart = new Date(startDateValue);
                const vStart = new Date(voucherStartDate);
                matchStartDate = vStart >= filterStart;
            }
            if (endDateValue) {
                const filterEnd = new Date(endDateValue);
                filterEnd.setHours(23, 59, 59, 999);
                const vEnd = new Date(voucherEndDate);
                matchEndDate = vEnd <= filterEnd;
            }
            if (matchCode && matchStartDate && matchEndDate) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    function formatDateTime(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleString('vi-VN');
    }

    function getStatus(start, end, isActive) {
        if (!isActive) {
            return `<span class="status-expired">Không kích hoạt</span>`;
        }
        const today = new Date();
        const s = new Date(start);
        const e = new Date(end);
        if (today >= s && today <= e) {
            return `<span class="status-active">Đang áp dụng</span>`;
        }
        return `<span class="status-expired">Hết hạn</span>`;
    }

    applyFilterBtn.addEventListener('click', filterVouchers);

    showAllBtn.addEventListener('click', () => {
        voucherCodeSearch.value = '';
        startDateFilter.value = '';
        endDateFilter.value = '';
        const allRows = dealList.querySelectorAll('tr');
        allRows.forEach(row => {
            row.style.display = '';
        });
    });

    addBtn?.addEventListener("click", () => {
        editingId = null;
        modalTitle.textContent = "Tạo voucher mới";
        form.reset();
        document.getElementById("isActive").checked = true;
        modal.classList.add("show");
    });

    closeModal?.addEventListener("click", () => {
        modal.classList.remove("show");
    });

    window.addEventListener("click", e => {
        if (e.target === modal) modal.classList.remove("show");
    });

    window.editVoucher = async (id) => {
        try {
            const response = await fetch(`/admin/api/vouchers/${id}`);
            const voucher = await response.json();
            editingId = id;
            modalTitle.textContent = "Chỉnh sửa voucher";
            document.getElementById("code").value = voucher.code;
            document.getElementById("description").value = voucher.description || '';
            document.getElementById("discount").value = voucher.discountPercent;
            document.getElementById("maxUses").value = voucher.maxUses || '';
            document.getElementById("startDate").value = formatDateTimeLocal(voucher.startDate);
            document.getElementById("endDate").value = formatDateTimeLocal(voucher.endDate);
            document.getElementById("isActive").checked = voucher.isActive;
            modal.classList.add("show");
        } catch (error) {
            console.error('Lỗi khi tải voucher:', error);
            alert('Không thể tải thông tin voucher');
        }
    };

    window.deleteVoucher = async (id) => {
        if (!confirm('Bạn có chắc chắn muốn xóa voucher này?')) {
            return;
        }
        try {
            const response = await fetch(`/admin/api/vouchers/${id}`, {
                method: 'DELETE'
            });
            const result = await response.json();
            if (result.success) {
                alert(result.message);
                loadVouchers();
            } else {
                alert(result.message);
            }
        } catch (error) {
            console.error('Lỗi khi xóa voucher:', error);
            alert('Không thể xóa voucher');
        }
    };

    function formatDateTimeLocal(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    form?.addEventListener("submit", async (e) => {
        e.preventDefault();
        const voucherData = {
            code: document.getElementById("code").value.trim(),
            description: document.getElementById("description").value.trim(),
            discountPercent: parseInt(document.getElementById("discount").value),
            maxUses: document.getElementById("maxUses").value ? parseInt(document.getElementById("maxUses").value) : null,
            startDate: document.getElementById("startDate").value,
            endDate: document.getElementById("endDate").value,
            isActive: document.getElementById("isActive").checked
        };
        try {
            let response;
            if (editingId === null) {
                response = await fetch('/admin/api/vouchers', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(voucherData)
                });
            } else {
                response = await fetch(`/admin/api/vouchers/${editingId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(voucherData)
                });
            }
            const result = await response.json();
            if (result.success) {
                alert(result.message);
                modal.classList.remove("show");
                loadVouchers();
            } else {
                alert(result.message);
            }
        } catch (error) {
            console.error('Lỗi khi lưu voucher:', error);
            alert('Không thể lưu voucher');
        }
    });
    loadVouchers();
});