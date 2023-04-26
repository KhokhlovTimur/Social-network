let tokens;

$(document).ready(function () {
    $('#clk').click(generateRequest)
});

function generateRequest() {

    console.log('Form submitted');

    let rawUsername = $('#username');
    let rawPassword = $('#password');
    let username = rawUsername.val();
    let password = rawPassword.val();
    rawUsername.val('');
    rawPassword.val('');

    const details = {
        'username': username,
        'password': password
    };

    console.log('sending request...')

    $.ajax({
        url: '/auth/token',
        method: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: $.param(details),
        dataType: 'json',
        statusCode: {
            // 401: ,
            // 403:
        },
        success: function (res) {
            onSuccessAuth(res);
        }
    })
}

function onSuccessAuth(result) {
    console.log('successful auth')

    tokens = result;
    localStorage.setItem('accessToken', tokens['accessToken']);
    localStorage.setItem('refreshToken', tokens['refreshToken']);

    $.ajaxSetup({
        headers: {
            'Authorization': 'Bearer ' + tokens['accessToken']
        }
    })
}

function refreshAccessToken() {

}

setInterval(refreshAccessToken, 1700000);

$('#test').click(function () {
    $.ajax({
        url: '/api/chats/1',
        method: 'GET',
        success: function (res) {
            window.location.href = "/chats"
        }
    })
})