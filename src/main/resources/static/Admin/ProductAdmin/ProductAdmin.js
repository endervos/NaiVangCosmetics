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

// ====== Data mẫu (đÃ BỎ channels) ======
let products = [
  {
    sku: "NV-SER-001",
    name: "Serum Vitamin C 30ml",
    price: 299000,
    stock: 12,
    status: "published",
    category: "skin-care",
    image: "https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?q=80&w=400"
  },
  {
    sku: "NV-TON-002",
    name: "Toner Hoa Cúc 150ml",
    price: 189000,
    stock: 0,
    status: "hidden",
    category: "skin-care",
    image: "https://images.unsplash.com/photo-1615397349754-9f1f33e8efec?q=80&w=400"
  },
  {
    sku: "NV-LIP-003",
    name: "Son Lì Satin #08",
    price: 239000,
    stock: 4,
    status: "draft",
    category: "makeup",
    image: "https://images.unsplash.com/photo-1585386959984-a41552231658?q=80&w=400"
  },
  {
    sku: "NV-SHA-004",
    name: "Dầu gội phục hồi 300ml",
    price: 159000,
    stock: 27,
    status: "published",
    category: "hair",
    image: "https://images.unsplash.com/photo-1505575972945-270b6aebfdee?q=80&w=400"
  }
];

// ====== State & DOM ======
const tbody = document.getElementById('productTbody');
const paginationEl = document.getElementById('pagination');
const searchInput = document.getElementById('searchInput');
const filterCategory = document.getElementById('filterCategory');
const filterStatus = document.getElementById('filterStatus');
// const filterChannel = document.getElementById('filterChannel'); // ĐÃ BỎ
const filterStock = document.getElementById('filterStock');
const btnResetFilters = document.getElementById('btnResetFilters');
const checkAll = document.getElementById('checkAll');
const toast = document.getElementById('toast');

const btnAddProduct = document.getElementById('btnAddProduct');
const btnBulkPublish = document.getElementById('btnBulkPublish');
const btnBulkUnpublish = document.getElementById('btnBulkUnpublish');
const btnBulkDelete = document.getElementById('btnBulkDelete');

// Modals
const priceModal = document.getElementById('priceModal');
const stockModal = document.getElementById('stockModal');
const productModal = document.getElementById('productModal');

const priceSku = document.getElementById('priceSku');
const priceCurrent = document.getElementById('priceCurrent');
const priceNew = document.getElementById('priceNew');
const priceEffective = document.getElementById('priceEffective');
const btnSavePrice = document.getElementById('btnSavePrice');

const stockSku = document.getElementById('stockSku');
const stockCurrent = document.getElementById('stockCurrent');
const stockDelta = document.getElementById('stockDelta');
const stockReason = document.getElementById('stockReason');
const btnSaveStock = document.getElementById('btnSaveStock');

const formSku = document.getElementById('formSku');
const formName = document.getElementById('formName');
const formCategory = document.getElementById('formCategory');
const formPrice = document.getElementById('formPrice');
const formStock = document.getElementById('formStock');
const formStatus = document.getElementById('formStatus');
const formImage = document.getElementById('formImage');
const formImageFile = document.getElementById('formImageFile');     // mới
const formImagePreview = document.getElementById('formImagePreview'); // mới
const btnSaveProduct = document.getElementById('btnSaveProduct');
const productForm = document.getElementById('productForm');

let currentPage = 1;
const pageSize = 6;
let currentEditIndex = null;    // index của product đang edit
let selection = new Set();      // sku chọn checkbox

// ====== Utils ======
function fmtMoney(n){ return n.toLocaleString('vi-VN'); }
function showToast(msg){
  toast.textContent = msg;
  toast.classList.add('show');
  setTimeout(()=>toast.classList.remove('show'), 1800);
}
function openModal(el){ el.classList.add('show'); el.setAttribute('aria-hidden','false'); }
function closeModal(el){ el.classList.remove('show'); el.setAttribute('aria-hidden','true'); }
document.querySelectorAll('[data-close]').forEach(btn=>{
  btn.addEventListener('click', ()=> {
    const id = btn.getAttribute('data-close');
    const m = document.getElementById(id);
    if(m) closeModal(m);
  });
});
window.addEventListener('keydown', (e)=> { if(e.key === 'Escape'){ [priceModal,stockModal,productModal].forEach(closeModal); }});

// ====== Filters & Search (ĐÃ BỎ kênh) ======
function applyFilters(list){
  const q = (searchInput.value || '').trim().toLowerCase();
  const cat = filterCategory.value;
  const st = filterStatus.value;
  const stk = filterStock.value;

  return list.filter(p=>{
    const matchQ = !q || p.name.toLowerCase().includes(q) || p.sku.toLowerCase().includes(q);
    const matchCat = !cat || p.category === cat;
    const matchSt = !st || p.status === st;
    let matchStock = true;
    if(stk === 'low') matchStock = p.stock <= 5 && p.stock > 0;
    if(stk === 'oos') matchStock = p.stock === 0;
    if(stk === 'in')  matchStock = p.stock > 0;
    return matchQ && matchCat && matchSt && matchStock;
  });
}

function render(){
  const filtered = applyFilters(products);
  const total = filtered.length;
  const pages = Math.max(1, Math.ceil(total / pageSize));
  if(currentPage > pages) currentPage = pages;

  const start = (currentPage - 1) * pageSize;
  const rows = filtered.slice(start, start + pageSize);

  // tbody (ĐÃ BỎ cột kênh)
  tbody.innerHTML = rows.map(p=> `
    <tr data-sku="${p.sku}">
      <td><input type="checkbox" class="row-check" ${selection.has(p.sku)?'checked':''}></td>
      <td>${p.sku}</td>
      <td>
        <div class="prod">
          <img src="${p.image || 'https://dummyimage.com/80x80/e5e7eb/9ca3af.png&text=IMG'}" alt="">
          <div>
            <div style="font-weight:600">${p.name}</div>
            <div style="color:#6b7280; font-size:12px">Danh mục: ${p.category}</div>
          </div>
        </div>
      </td>
      <td>${fmtMoney(p.price)}</td>
      <td>${p.stock}</td>
      <td>
        <span class="badge ${p.status}">
          ${p.status === 'published' ? '<i class="fa-solid fa-check-circle"></i> Published'
            : p.status === 'draft' ? '<i class="fa-regular fa-file-lines"></i> Draft'
            : '<i class="fa-regular fa-eye-slash"></i> Hidden'}
        </span>
      </td>
      <td>
        <div class="actions">
          <button class="icon-btn act-edit" title="Sửa"><i class="fa-solid fa-pen"></i></button>
          <button class="icon-btn act-price" title="Cập nhật giá"><i class="fa-solid fa-tag"></i></button>
          <button class="icon-btn act-stock" title="Điều chỉnh tồn"><i class="fa-solid fa-boxes-stacked"></i></button>
          <button class="icon-btn act-visibility" title="Ẩn/Hiện"><i class="fa-solid fa-eye-slash"></i></button>
          <button class="icon-btn act-delete" title="Xóa" style="border-color:#f3c2c2;"><i class="fa-solid fa-trash" style="color:#e04747"></i></button>
        </div>
      </td>
    </tr>
  `).join('');

  // row events
  tbody.querySelectorAll('.row-check').forEach(cb=>{
    cb.addEventListener('change', (e)=>{
      const sku = e.target.closest('tr').dataset.sku;
      if(e.target.checked) selection.add(sku); else selection.delete(sku);
    });
  });

  tbody.querySelectorAll('.act-edit').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const sku = btn.closest('tr').dataset.sku;
      openEditProduct(sku);
    });
  });
  tbody.querySelectorAll('.act-price').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const sku = btn.closest('tr').dataset.sku;
      openPriceModal(sku);
    });
  });
  tbody.querySelectorAll('.act-stock').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const sku = btn.closest('tr').dataset.sku;
      openStockModal(sku);
    });
  });
  tbody.querySelectorAll('.act-visibility').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const sku = btn.closest('tr').dataset.sku;
      toggleVisibility(sku);
    });
  });
  tbody.querySelectorAll('.act-delete').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const sku = btn.closest('tr').dataset.sku;
      if(confirm(`Xóa sản phẩm ${sku}?`)){
        products = products.filter(p=>p.sku !== sku);
        selection.delete(sku);
        showToast('Đã xóa sản phẩm');
        render();
      }
    });
  });

  // pagination
  paginationEl.innerHTML = `
    <div class="page-info">Hiển thị ${Math.min(total, start+1)}–${Math.min(total, start + rows.length)} / ${total}</div>
    <div class="page-controls">
      <button ${currentPage===1?'disabled':''} id="btnPrev"><i class="fa-solid fa-chevron-left"></i></button>
      <span style="padding:8px 10px;border:1px solid var(--line);border-radius:10px;">Trang ${currentPage}/${pages}</span>
      <button ${currentPage===pages?'disabled':''} id="btnNext"><i class="fa-solid fa-chevron-right"></i></button>
    </div>
  `;
  const prev = document.getElementById('btnPrev');
  const next = document.getElementById('btnNext');
  if(prev) prev.addEventListener('click', ()=>{ currentPage = Math.max(1, currentPage-1); render(); });
  if(next) next.addEventListener('click', ()=>{ currentPage = currentPage+1; render(); });

  // checkAll sync
  checkAll.checked = selection.size && rows.every(r=> selection.has(r.sku));
}

// ====== Actions ======
function toggleVisibility(sku){
  const p = products.find(x=>x.sku===sku);
  if(!p) return;
  p.status = (p.status === 'hidden') ? 'published' : 'hidden';
  showToast(p.status==='hidden'?'Đã ẩn sản phẩm':'Đã publish sản phẩm');
  render();
}

function openPriceModal(sku){
  const p = products.find(x=>x.sku===sku);
  if(!p) return;
  priceSku.value = p.sku;
  priceCurrent.value = p.price;
  priceNew.value = p.price;
  priceEffective.valueAsDate = new Date();
  openModal(priceModal);
}
btnSavePrice.addEventListener('click', ()=>{
  const sku = priceSku.value;
  const newPrice = parseInt(priceNew.value || '0', 10);
  if(isNaN(newPrice) || newPrice < 0){ alert('Giá không hợp lệ'); return; }
  const p = products.find(x=>x.sku===sku);
  if(!p) return;
  p.price = newPrice;
  closeModal(priceModal);
  showToast('Đã cập nhật giá');
  render();
});

function openStockModal(sku){
  const p = products.find(x=>x.sku===sku);
  if(!p) return;
  stockSku.value = p.sku;
  stockCurrent.value = p.stock;
  stockDelta.value = 0;
  stockReason.value = 'replenish';
  openModal(stockModal);
}
btnSaveStock.addEventListener('click', ()=>{
  const sku = stockSku.value;
  const delta = parseInt(stockDelta.value || '0', 10);
  if(isNaN(delta)){ alert('Số lượng điều chỉnh không hợp lệ'); return; }
  const p = products.find(x=>x.sku===sku);
  if(!p) return;
  p.stock = Math.max(0, p.stock + delta);
  closeModal(stockModal);
  showToast('Đã cập nhật tồn kho');
  render();
});

function openAddProduct(){
  currentEditIndex = null;
  productForm.reset();
  formStatus.value = 'published';
  openModal(productModal);
}
function openEditProduct(sku){
  const idx = products.findIndex(x=>x.sku===sku);
  if(idx<0) return;
  currentEditIndex = idx;
  const p = products[idx];
  formSku.value = p.sku;
  formName.value = p.name;
  formCategory.value = p.category;
  formPrice.value = p.price;
  formStock.value = p.stock;
  formStatus.value = p.status;
  formImage.value = p.image || '';
  // (ĐÃ BỎ: set checkbox kênh)
  openModal(productModal);
}

btnSaveProduct.addEventListener('click', ()=>{
  const sku = formSku.value.trim();
  const name = formName.value.trim();
  if(!sku || !name){ alert('SKU và Tên sản phẩm là bắt buộc'); return; }

  const obj = {
    sku,
    name,
    category: formCategory.value,
    price: parseInt(formPrice.value || '0', 10),
    stock: parseInt(formStock.value || '0', 10),
    status: formStatus.value,
    image: formImage.value.trim()
  };

  if(currentEditIndex === null){
    if(products.some(p=>p.sku===sku)){ alert('SKU đã tồn tại'); return; }
    products.unshift(obj);
    showToast('Đã thêm sản phẩm');
  }else{
    products[currentEditIndex] = obj;
    showToast('Đã lưu thay đổi');
  }
  closeModal(productModal);
  render();
});

// ====== Bulk actions ======
function getSelected(){ return Array.from(selection.values()); }

btnBulkPublish.addEventListener('click', ()=>{
  const sel = getSelected();
  if(!sel.length){ alert('Chưa chọn sản phẩm'); return; }
  products = products.map(p=> sel.includes(p.sku) ? {...p, status:'published'} : p);
  showToast(`Publish ${sel.length} sản phẩm`);
  render();
});

btnBulkUnpublish.addEventListener('click', ()=>{
  const sel = getSelected();
  if(!sel.length){ alert('Chưa chọn sản phẩm'); return; }
  products = products.map(p=> sel.includes(p.sku) ? {...p, status:'hidden'} : p);
  showToast(`Unpublish ${sel.length} sản phẩm`);
  render();
});

btnBulkDelete.addEventListener('click', ()=>{
  const sel = getSelected();
  if(!sel.length){ alert('Chưa chọn sản phẩm'); return; }
  if(!confirm(`Xóa ${sel.length} sản phẩm?`)) return;
  products = products.filter(p=> !sel.includes(p.sku));
  selection.clear();
  showToast('Đã xóa các sản phẩm đã chọn');
  render();
});

checkAll.addEventListener('change', (e)=>{
  const filtered = applyFilters(products);
  const start = (currentPage - 1) * pageSize;
  const rows = filtered.slice(start, start + pageSize);
  if(e.target.checked){
    rows.forEach(r=> selection.add(r.sku));
  }else{
    rows.forEach(r=> selection.delete(r.sku));
  }
  render();
});

// ====== Search & Filters (ĐÃ BỎ filterChannel) ======
[searchInput, filterCategory, filterStatus, filterStock].forEach(el=>{
  el && el.addEventListener('input', ()=>{ currentPage = 1; render(); });
});
btnResetFilters.addEventListener('click', ()=>{
  searchInput.value = '';
  filterCategory.value = '';
  filterStatus.value = '';
  // filterChannel.value = ''; // ĐÃ BỎ
  filterStock.value = '';
  currentPage = 1;
  render();
});

// Add product
btnAddProduct.addEventListener('click', openAddProduct);

// ====== Init ======
render();
