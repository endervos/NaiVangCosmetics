$(document).ready(function(){
  $('.slider').slick({
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: true,
    prevArrow: $('.nav.prev'),
    nextArrow: $('.nav.next'),
    autoplay: true,
    autoplaySpeed: 3000,
    dots: true,
    infinite: true,      // 🔑 loop vô hạn
    speed: 600,          // tốc độ chuyển slide
    cssEase: 'ease-in-out' // chuyển động mượt
  });
});
