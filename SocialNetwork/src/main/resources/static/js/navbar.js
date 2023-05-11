let navbarImg = $('.navbarImage');
$(document).ready(function () {
    navbarImg.click(redirect);
});

function redirect(event) {
    let path = event.target.getAttribute('value');
    window.location.href = '/app/' + path;
}


$('ul li').on('click', function () {
    $('li').removeClass('active');
    $(this).addClass('active');
});