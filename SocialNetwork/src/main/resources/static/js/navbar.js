let navbarImg = $('.navbarImage');
$(document).ready(function () {
    navbarImg.click(redirect);
});

async function redirect(event) {
    let path = event.target.getAttribute('value');
    if (path === 'profile') {
        let token = localStorage.getItem('refreshToken');
        let payload = token.split('.')[1];
        payload = window.atob(payload);
        payload = JSON.parse(payload);
        currUsername = payload['sub'];
        path += '/' + currUsername;
    }
    window.location.href = '/app/' + path;
}


$('ul li').on('click', function () {
    $('li').removeClass('active');
    $(this).addClass('active');
});
