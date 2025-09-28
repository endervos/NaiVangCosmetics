let reviews = [
    {id:1, productId:"A001", userId:"U001", userName:"Nguyễn Văn A", rating:5, comment:"Rất tốt!", reply:"Cảm ơn bạn!", date:"2025-09-25"},
    {id:2, productId:"A001", userId:"U002", userName:"Trần Thị B", rating:4, comment:"Ổn", reply:"", date:"2025-09-24"},
    {id:3, productId:"B002", userId:"U003", userName:"Lê Văn C", rating:3, comment:"Bình thường", reply:"", date:"2025-09-26"},
    {id:4, productId:"B002", userId:"U004", userName:"Phạm Thị D", rating:5, comment:"Tuyệt vời!", reply:"", date:"2025-09-23"},
    {id:5, productId:"C003", userId:"U005", userName:"Ngô Văn E", rating:2, comment:"Không hài lòng", reply:"", date:"2025-09-25"},
    {id:6, productId:"C004", userId:"U006", userName:"Trần Văn F", rating:4, comment:"Ok", reply:"", date:"2025-09-22"},
    {id:7, productId:"C005", userId:"U007", userName:"Nguyễn Thị G", rating:3, comment:"Tạm ổn", reply:"", date:"2025-09-20"},
    {id:8, productId:"C006", userId:"U008", userName:"Lê Thị H", rating:5, comment:"Rất tốt!", reply:"", date:"2025-09-21"},
    {id:9, productId:"C007", userId:"U009", userName:"Phạm Văn I", rating:4, comment:"Ổn", reply:"", date:"2025-09-19"},
    {id:10, productId:"C008", userId:"U010", userName:"Ngô Thị J", rating:5, comment:"Tuyệt!", reply:"", date:"2025-09-18"},
    {id:11, productId:"C009", userId:"U011", userName:"Trần Văn K", rating:2, comment:"Không hài lòng", reply:"", date:"2025-09-17"}
];

const reviewTableBody = document.querySelector("#review-table tbody");
const productSearch = document.getElementById("product-search");
const sortFilter = document.getElementById("sort-filter");
const applyFilterBtn = document.getElementById("apply-filter");
const showAllBtn = document.getElementById("show-all");
const prevPageBtn = document.getElementById("prev-page");
const nextPageBtn = document.getElementById("next-page");
const pageInfo = document.getElementById("page-info");

let currentPage = 1;
const rowsPerPage = 10;

function renderTable() {
    reviewTableBody.innerHTML = "";
    let filtered = [...reviews];

    // Filter theo ID sản phẩm
    const keyword = productSearch.value.trim().toLowerCase();
    if(keyword){
        filtered = filtered.filter(r => r.productId.toLowerCase().includes(keyword));
    }

    // Sort theo ngày
    const sortValue = sortFilter.value;
    filtered.sort((a,b) => {
        if(sortValue === "latest") return new Date(b.date) - new Date(a.date);
        return new Date(a.date) - new Date(b.date);
    });

    // Phân trang
    const totalPages = Math.ceil(filtered.length / rowsPerPage);
    if(currentPage > totalPages) currentPage = totalPages;
    if(currentPage < 1) currentPage = 1;

    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const paginated = filtered.slice(start, end);

    paginated.forEach((r) => {
        const tr = document.createElement("tr");
        const replyDisplay = r.reply ? `<div class="review-reply">${r.reply}</div>` : "";
        tr.innerHTML = `
            <td>${r.productId}</td>
            <td>${r.userName}</td>
            <td>${r.rating} ⭐</td>
            <td style="width:35%">${r.comment}${replyDisplay}</td>
            <td>
                <input type="text" class="reply-input" value="${r.reply || ""}" placeholder="Nhập reply...">
                <button class="reply-btn">Gửi</button>
            </td>
            <td>${r.date}</td>
            <td>
                <button class="delete-btn">Xóa</button>
            </td>
        `;
        reviewTableBody.appendChild(tr);

        const replyInput = tr.querySelector(".reply-input");
        const replyBtn = tr.querySelector(".reply-btn");
        const deleteBtn = tr.querySelector(".delete-btn");

        replyBtn.addEventListener("click", () => {
            r.reply = replyInput.value.trim();
            renderTable();
        });

        deleteBtn.addEventListener("click", () => {
            if(confirm("Bạn có chắc muốn xóa đánh giá này?")){
                reviews.splice(reviews.indexOf(r),1);
                renderTable();
            }
        });
    });

    pageInfo.textContent = `Trang ${currentPage} / ${totalPages || 1}`;
}

// Nút phân trang
prevPageBtn.addEventListener("click", () => {
    currentPage--;
    renderTable();
});
nextPageBtn.addEventListener("click", () => {
    currentPage++;
    renderTable();
});

// Áp dụng filter
applyFilterBtn.addEventListener("click", () => { currentPage = 1; renderTable(); });

// Hiển thị tất cả
showAllBtn.addEventListener("click", () => {
    productSearch.value = "";
    sortFilter.value = "latest";
    currentPage = 1;
    renderTable();
});

// Load lần đầu
document.addEventListener("DOMContentLoaded", () => {
    renderTable();
});
