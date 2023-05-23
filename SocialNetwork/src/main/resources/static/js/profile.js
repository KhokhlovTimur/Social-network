$(document).ready(function () {
    $('.logout-button').click(logout);
});

function logout() {

    $.ajax({
        url: '/api/logout',
        method: 'POST',
        contentType: false,
        processData: false,
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function () {
            window.location.href = '/app/login';
        }
    })
}