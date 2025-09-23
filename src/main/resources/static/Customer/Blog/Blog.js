document.addEventListener("DOMContentLoaded", () => {
  const posts = document.querySelectorAll(".post");

  posts.forEach((post) => {
    post.addEventListener("click", () => {
      alert(`Bạn vừa mở bài viết: "${post.querySelector("h2").textContent}"`);
    });
  });
});

// Slider banner
let slideIndex = 0;
const slides = document.querySelectorAll(".slide");
const prevBtn = document.querySelector(".prev");
const nextBtn = document.querySelector(".next");

function showSlide(index) {
  slides.forEach((slide, i) => {
    slide.classList.remove("active");
    if (i === index) {
      slide.classList.add("active");
    }
  });
}

function nextSlide() {
  slideIndex = (slideIndex + 1) % slides.length;
  showSlide(slideIndex);
}

function prevSlide() {
  slideIndex = (slideIndex - 1 + slides.length) % slides.length;
  showSlide(slideIndex);
}

nextBtn.addEventListener("click", nextSlide);
prevBtn.addEventListener("click", prevSlide);

// Tự động chuyển slide sau 5s
setInterval(nextSlide, 5000);

// Hiển thị slide đầu tiên
showSlide(slideIndex);
