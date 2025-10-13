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
});

const ctxRevenue = document.getElementById('revenueChart').getContext('2d');
new Chart(ctxRevenue, {
  type: 'line',
  data: {
    labels: ['Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9'],
    datasets: [{
      label: 'Doanh thu (triệu ₫)',
      data: [50, 65, 70, 55, 90, 80],
      borderColor: '#36a2eb',
      backgroundColor: 'rgba(54, 162, 235, 0.2)',
      borderWidth: 2,
      fill: true,
      tension: 0.3,
      pointBackgroundColor: '#1d72b8'
    }]
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,  // ✅ mặc định auto co theo box
    plugins: {
      legend: {
        display: true,
        position: 'bottom'
      },
      title: {
        display: false
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: { color: '#555' },
        grid: {
          drawOnChartArea: false,
          drawBorder: true,
          drawTicks: false
        }
      },
      x: {
        ticks: { color: '#555' },
        grid: {
          display: false,
          drawBorder: true
        }
      }
    },
  }
});


// Biểu đồ tròn danh mục sản phẩm
const ctxCategory = document.getElementById('categoryChart').getContext('2d');
new Chart(ctxCategory, {
  type: 'doughnut',
  data: {
    labels: ['Chăm sóc da mặt', 'Trang điểm', 'Chăm sóc cơ thể ', 'Chăm sóc tóc', 'Dược mỹ phẩm', 'Thực phẩm chức năng'],
    datasets: [{
      data: [45, 30, 15, 10, 12, 8],
      backgroundColor: ['#2aa198', '#268bd2', '#b58900', '#dc322f', '#6c5ce7', '#FFB347'],
      borderWidth: 0,
      hoverOffset: 10
    }]
  },
  options: {
    responsive: true,
    maintainAspectRatio: true, // ✅ giữ tỉ lệ vuông để tròn đều
    aspectRatio: 1,            // ✅ ép canvas luôn là hình vuông
    plugins: {
      legend: {
        position: 'bottom',
        align: 'start',
        labels: { color: '#555', boxWidth: 20, padding: 20, }
      },
      title: {
        display: false
      }
    },
    layout: {
      padding: 10
    },
    cutout: '65%' // ✅ điều chỉnh độ dày vòng (tùy thích)
  }
});

const ctxTopProducts = document.getElementById('topProductsChart').getContext('2d');

  new Chart(ctxTopProducts, {
    type: 'bar',
    data: {
      labels: [
        'Serum Vitamin C',
        'Kem Dưỡng Ẩm',
        'Sữa Rửa Mặt',
        'Toner Hoa Cúc',
        'Mặt Nạ Đất Sét',
        'Kem Chống Nắng',
        'Nước Tẩy Trang',
        'Tinh Chất Retinol',
        'Dầu Gội Thảo Dược',
        'Son Dưỡng Môi'
      ],
      datasets: [{
        label: 'Số lượng bán',
        data: [500, 350, 480, 335, 670, 900, 580, 665, 700, 440],
        backgroundColor: [
          '#268bd2', '#2aa198', '#b58900', '#dc322f', '#6c5ce7',
          '#FFB347', '#20c997', '#ff7f50', '#9b59b6', '#00bcd4'
        ],
        borderRadius: 0,
        barThickness: 45,          // ✅ cột to hơn
        categoryPercentage: 0.75,  // ✅ giảm khoảng cách giữa các cột
        barPercentage: 0.9         // ✅ tối ưu khoảng cách trong mỗi nhóm
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          grid: { display: false, drawBorder: false },
          ticks: { font: { size: 12 }, maxRotation: 40, minRotation: 40 },
          title: { display: false }
        },
        y: {
          grid: { display: false, drawBorder: false },
          beginAtZero: true,
          title: {
            display: true,
            text: 'Số lượng bán',
            font: { size: 13, weight: 'bold' },
            padding: { bottom: 10, right: 20 }
          },
          ticks: { stepSize: 50 }
        }
      },
      plugins: {
        legend: { display: false },
        title: {
          display: true,
          text: 'TOP 10 SẢN PHẨM BÁN CHẠY NHẤT TRONG THÁNG NÀY',
          align: 'center',
          position: 'top', // ✅ căn giữa trên cùng vùng biểu đồ
          color: '#000',
          font: { size: 16, weight: 'bold' },
          padding: { top: 10, bottom: 20 }
        },
        tooltip: {
          callbacks: {
            label: (context) => ' ' + context.parsed.y + ' sản phẩm'
          }
        }
      },
      layout: {
        padding: { top: 20, bottom: 10, left: 10, right: 10 }
      }
    }
  });


