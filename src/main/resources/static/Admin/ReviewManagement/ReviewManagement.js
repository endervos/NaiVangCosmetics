// Giả sử bạn có dữ liệu mẫu
let reviews = [
    { productName: "Sản phẩm 1", customerName: "Nguyễn Văn A", rating: 4, comment: "Rất tốt", date: "2025-09-30 9:30", id: 1 },
    { productName: "Sản phẩm 2", customerName: "Trần Thị B", rating: 5, comment: "Tuyệt vời", date: "2025-09-29 16:12", id: 2 },
    { productName: "Sản phẩm 3", customerName: "Lê Thị C", rating: 3, comment: "Bình thường", date: "2025-09-28 3:02", id: 3 },
    { productName: "Sản phẩm 4", customerName: "Vũ Minh D", rating: 2, comment: "Không hài lòng", date: "2025-09-27 19:05", id: 4 },
    { productName: "Sản phẩm 5", customerName: "Phạm Quỳnh E", rating: 5, comment: "Sản phẩm chất lượng tuyệt vời!", date: "2025-09-26 20:10", id: 5 },
    { productName: "Sản phẩm 6", customerName: "Đỗ Minh F", rating: 4, comment: "Tốt nhưng có thể cải thiện", date: "2025-09-25 12:30", id: 6 },
    { productName: "Sản phẩm 7", customerName: "Hoàng Hải G", rating: 1, comment: "Không như mong đợi", date: "2025-09-24 9:12", id: 7 },
    { productName: "Sản phẩm 8", customerName: "Lê Thị H", rating: 4, comment: "Chất lượng ổn", date: "2025-09-23 3:50", id: 8 },
    { productName: "Sản phẩm 9", customerName: "Nguyễn Phương I", rating: 3, comment: "Bình thường", date: "2025-09-22 12:30", id: 9 },
    { productName: "Sản phẩm 10", customerName: "Trần Minh K", rating: 2, comment: "Chưa đạt yêu cầu", date: "2025-09-21 5:12", id: 10 },
    { productName: "Sản phẩm 11", customerName: "Nguyễn Văn L", rating: 3, comment: "Cần cải thiện", date: "2025-09-20 9:10", id: 11 },
    { productName: "Sản phẩm 12", customerName: "Trần Thi L", rating: 5, comment: "Rất hài lòng", date: "2025-09-19 18:20", id: 12 },
    { productName: "Sản phẩm 13", customerName: "Lê Thị M", rating: 4, comment: "Ok nhưng có thể tốt hơn", date: "2025-09-18 14:30", id: 13 },
    { productName: "Sản phẩm 14", customerName: "Phạm Quỳnh N", rating: 2, comment: "Không như mong đợi", date: "2025-09-17 22:05", id: 14 },
    { productName: "Sản phẩm 15", customerName: "Đỗ Minh P", rating: 5, comment: "Tuyệt vời", date: "2025-09-16 15:50", id: 15 }
];

// Hàm hiển thị sao
function getRatingStars(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            stars += '<span class="star">★</span>';
        } else {
            stars += '<span class="star gray">★</span>';
        }
    }
    return stars;
}

// Hàm xóa đánh giá
function deleteReview(id) {
    reviews = reviews.filter(review => review.id !== id);
    currentPage = 1; // Đặt lại trang về 1 khi xóa
    const filteredReviews = getFilteredAndSortedReviews();
    renderReviews(filteredReviews);
    renderPagination(filteredReviews);
}

// Hàm lấy danh sách đã lọc và sắp xếp
function getFilteredAndSortedReviews() {
    const searchQuery = document.getElementById('product-search').value.toLowerCase();
    const sortOrder = document.getElementById('sort-filter').value;

    let filteredReviews = reviews.filter(review => review.productName.toLowerCase().includes(searchQuery));

    if (sortOrder === "latest") {
        filteredReviews.sort((a, b) => new Date(b.date) - new Date(a.date));
    } else {
        filteredReviews.sort((a, b) => new Date(a.date) - new Date(b.date));
    }

    return filteredReviews;
}

// Hiển thị danh sách đánh giá
function renderReviews(reviewList) {
    const tbody = document.querySelector('#review-table tbody');
    tbody.innerHTML = ''; // Xóa dữ liệu cũ

    const reviewsToShow = paginateReviews(reviewList); // Luôn áp dụng phân trang

    reviewsToShow.forEach(review => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${review.productName}</td>
            <td>${review.customerName}</td>
            <td class="rating">${getRatingStars(review.rating)}</td>
            <td>${review.comment}</td>
            <td>${new Date(review.date).toLocaleString('vi-VN', { 
                hour: '2-digit', minute: '2-digit', hour12: false,
                year: 'numeric', month: '2-digit', day: '2-digit' 
            })}</td>
            <td><button class="delete-btn" data-id="${review.id}">Xóa</button></td>
        `;
        tbody.appendChild(row);
    });

    // Thêm sự kiện xóa
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const reviewId = parseInt(e.target.dataset.id);
            deleteReview(reviewId);
        });
    });
}

// Phân trang
let currentPage = 1;
const reviewsPerPage = 10; // Giới hạn hiển thị 10 bình luận mỗi trang

function paginateReviews(reviewList) {
    const startIndex = (currentPage - 1) * reviewsPerPage;
    const endIndex = startIndex + reviewsPerPage;
    return reviewList.slice(startIndex, endIndex);
}

function renderPagination(reviewList) {
    const totalPages = Math.ceil(reviewList.length / reviewsPerPage);
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');

    prevPageBtn.disabled = currentPage === 1;
    nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;

    pageInfo.textContent = `Trang ${currentPage} / ${totalPages || 1}`;
}

// Sự kiện áp dụng bộ lọc
document.getElementById('apply-filter').addEventListener('click', () => {
    currentPage = 1; // Đặt lại trang về 1 khi áp dụng bộ lọc
    const filteredReviews = getFilteredAndSortedReviews();
    renderReviews(filteredReviews);
    renderPagination(filteredReviews);
});

// Sự kiện hiển thị tất cả
document.getElementById('show-all').addEventListener('click', () => {
    currentPage = 1; // Đặt lại trang về 1
    document.getElementById('product-search').value = ''; // Xóa tìm kiếm
    document.getElementById('sort-filter').value = 'latest'; // Đặt lại sắp xếp
    renderReviews(reviews);
    renderPagination(reviews);
});

// Sự kiện chuyển trang trước
document.getElementById('prev-page').addEventListener('click', () => {
    if (currentPage > 1) {
        currentPage--;
        const filteredReviews = getFilteredAndSortedReviews();
        renderReviews(filteredReviews);
        renderPagination(filteredReviews);
    }
});

// Sự kiện chuyển trang tiếp theo
document.getElementById('next-page').addEventListener('click', () => {
    const filteredReviews = getFilteredAndSortedReviews();
    const totalPages = Math.ceil(filteredReviews.length / reviewsPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        renderReviews(filteredReviews);
        renderPagination(filteredReviews);
    }
});

// Khi trang tải, hiển thị 10 đánh giá đầu tiên
document.addEventListener('DOMContentLoaded', function () {
    renderReviews(reviews);
    renderPagination(reviews);
});