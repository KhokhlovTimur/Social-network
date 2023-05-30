let tokens;

$(document).ready(function () {
    $('.signUp').submit(signUp);
    $('.signIn').submit(signIn);

    $(".log-in").click(function () {
        $('.err-message').empty();
        $('.signIn').addClass("active-dx");
        $('.signUp').addClass("inactive-sx");
        $('.signUp').removeClass("active-sx");
        $('.signIn').removeClass("inactive-dx");
        $('.back').prop('disabled', false);
        $('.login-submit').prop('disabled', false);
        $('.log-in').prop('disabled', true);
        $('.register-submit').prop('disabled', true);
    });

    $(".log-in").click();

    $(".back").click(function () {
        $('.err-message').empty();
        $(".signUp").addClass("active-sx");
        $(".signIn").addClass("inactive-dx");
        $(".signIn").removeClass("active-dx");
        $(".signUp").removeClass("inactive-sx");

        $('.back').prop('disabled', true);
        $('.login-submit').prop('disabled', true);
        $('.log-in').prop('disabled', false);
        $('.register-submit').prop('disabled', false);
    });
});

async function signUp(event) {
    event.preventDefault();
    $('.err-message').empty();

    let rawUsername = $('#username-registration');
    let rawPassword = $('#password');
    let rawPasswordVerify = $('#password-verify')
    let username = rawUsername.val();
    let password = rawPassword.val();
    let name = $('#name').val();
    let gender;

    let male = $('#Male');
    let female = $('#Female');
    if (male.is(':checked')) {
        gender = 'Male';
    } else if (female.is(':checked')){
        gender = 'Female';
    }

    let age = $('#age');
    age.css('border-bottom', '1px solid #a4c2f3');
    if (isNaN(parseInt(age.val()))) {
        age.css('border-bottom', '1px solid #ff6363');
        age.val('');
        return;
    }

    let surname = $('#surname').val();
    let passwordVerify = rawPasswordVerify.val();

    if (password !== passwordVerify) {
        rawPasswordVerify.css('border-bottom', '1px solid #ff6363');
        rawPasswordVerify.val('');
        return;
    }

    rawPasswordVerify.css('border-bottom', '1px solid #a4c2f3');

    rawUsername.val('');
    rawPassword.val('');
    rawPasswordVerify.val('');
    $('#name').val('');
    $('#surname').val('');

    const details = {
        'name': name,
        'surname': surname,
        'username': username,
        'password': password,
        'gender': gender,
        'age': parseInt(age.val())
    };

    age.val('');

    await generateRequestWithoutToken('/users', 'POST',
        function () {
            $('.log-in').prop('disabled', false);
            $('.log-in').click();
        },
        function (xhr) {
            onErrorAuth(xhr, $('.registration-err'));
        },
        'application/json', JSON.stringify(details));
}


function signIn(event) {
    $('#age').css('border-bottom', '1px solid #a4c2f3');
    $('.err-message').empty();
    event.preventDefault();
    console.log('Try to log in...');

    let rawUsername = $('#username-login');
    let rawPassword = $('#login-password');
    let username = rawUsername.val();
    let password = rawPassword.val();

    rawUsername.val('');
    rawPassword.val('');

    const details = {
        'username': username,
        'password': password
    };

    console.log('Sending request...')

    $.ajax({
        url: '/api/auth/token',
        method: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: $.param(details),
        dataType: 'json',
        error: function (xhr) {
            $('#username-login').css('border-bottom', '1px solid #ff6363');
            $('#login-password').css('border-bottom', '1px solid #ff6363');
        },
        success: function (res) {
            localStorage.setItem('entryTime', new Date().getTime().toString());
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


function generateRequestWithoutToken(url, method, successFunc, unSuccessFunc, contentType, data) {
    return new Promise((resolve, reject) => $.ajax({
        url: '/api' + url,
        method: method,
        contentType: contentType,
        dataType: 'json',
        data: data,
        success: function (res) {
            res = sanitize(res);
            successFunc(res);
            $('.registration-err').empty();
            $('.login-err').empty();
            resolve();
        }, error: function (xhr) {
            unSuccessFunc(xhr);
            reject();
        }
    }))
}

function onErrorAuth(xhr, object) {
    let contentType = xhr.getResponseHeader('Content-type');
    if (contentType != null && contentType === 'application/json') {
        let rawResponse = xhr.responseText;
        let response = JSON.parse(rawResponse);
        if (rawResponse !== null && response !== undefined) {
            for (let key in response) {
                if (response[key] !== null && response[key]['message'] !== undefined) {
                    response[key] = sanitize(response[key]);
                    object.append('<li>' + response[key]['message'] + '<li>');
                }
            }
        } else {
            console.log(rawResponse);
        }
    }
}
