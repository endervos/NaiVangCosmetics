// ========================
// PHẦN 1: SLIDER BANNER
// ========================

const track  = document.querySelector('.slider');
const slides = Array.from(document.querySelectorAll('.slide'));
const prevBtn = document.querySelector('.prev');
const nextBtn = document.querySelector('.next');

let index = 0;   // slide trung tâm

function centerTo(i, animate = true) {
  index = (i + slides.length) % slides.length;

  slides.forEach((s, k) => s.classList.toggle('active', k === index));

  const active = slides[index];
  const viewport = track.parentElement; // .inslideron
  const offset = active.offsetLeft + active.offsetWidth / 2 - viewport.clientWidth / 2;

  track.style.transition = animate ? 'transform 0.5s ease' : 'none';
  track.style.transform  = `translateX(${-offset}px)`;
}

nextBtn.addEventListener('click', () => centerTo(index + 1));
prevBtn.addEventListener('click', () => centerTo(index - 1));

let timer = setInterval(() => centerTo(index + 1), 4000);

track.addEventListener('mouseenter', () => clearInterval(timer));
track.addEventListener('mouseleave', () => {
  timer = setInterval(() => centerTo(index + 1), 4000);
});

window.addEventListener('resize', () => centerTo(index, false));

centerTo(0, false);

//Nút đăng nhập
document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("loginBtn");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      window.location.href = "../trangdangnhap/trangdangnhap.html";
    });
  }
});


