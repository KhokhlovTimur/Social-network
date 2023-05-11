let tokens;

$(document).ready(function () {
    $('#clk').click(generateRequest)
});

function generateRequest(event) {
    event.preventDefault();
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
        url: '/api/auth/token',
        method: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: $.param(details),
        dataType: 'json',
        statusCode: {
            401: function () {
                $("#password").css('background-color', '#E23E57')
            },
        },
        success: function (res) {
            onSuccessAuth(res);
        }
    })
}

function onSuccessAuth(result) {
    console.log('successful auth')

    tokens = result;
    localStorage.setItem('refreshToken', tokens['refreshToken']);
    localStorage.setItem('accessToken', tokens['accessToken']);

    window.location.href = '/app/feeds'
}

function generateRequestToRefreshToken() {
    $.ajax({
        url: '/api/auth/token',
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage['refreshToken']
        },
        statusCode: {
            401: function () {
                $("#password").css('background-color', '#E23E57')
            },
            // 403:
        },
        success: function (res) {
            onSuccessAuth(res);
        }
    })
}

setInterval(generateRequestToRefreshToken, 1700000);