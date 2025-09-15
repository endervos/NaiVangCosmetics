const track  = document.querySelector('.slider');
const slides = Array.from(document.querySelectorAll('.slide'));
const prevBtn = document.querySelector('.prev');
const nextBtn = document.querySelector('.next');

let index = 0;

function centerTo(i, animate = true) {
  index = (i + slides.length) % slides.length;
  slides.forEach((s, k) => s.classList.toggle('active', k === index));
  const active = slides[index];
  const viewport = track.parentElement;
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

$(document).ready(function(){
  $('.slider').slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: true,
    prevArrow: $('.nav.prev'),
    nextArrow: $('.nav.next'),
    autoplay: true,
    autoplaySpeed: 3000,
    dots: true
  });
});