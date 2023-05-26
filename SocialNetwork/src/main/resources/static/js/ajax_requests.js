let entryTime;
let passedTime;
let updateTime = 1600000;
let remainedTime;

$(document).ready(function () {
    if (!window.location.href.toString().includes('/app/login')) {
        let time = localStorage.getItem('deltaTime');
        entryTime = localStorage.getItem('entryTime');
        if (remainedTime < 60000) {
            updateToken();
        }

        if (time === null) {
            setTimeout(updateToken, updateTime);
        } else {
            console.log(parseInt(time))
            setTimeout(updateToken, parseInt(time));
        }
    }
})

window.addEventListener("beforeunload", function () {
    console.log('Switched tab');
    if (!window.location.href.toString().includes('/app/login')) {
        let endTime = new Date().getTime();
        passedTime = endTime - parseInt(localStorage.getItem('entryTime'));
        console.log(passedTime);
        remainedTime = updateTime - passedTime;
        console.log(remainedTime);

        if (remainedTime < 60000) {
            updateToken();
        }
        localStorage.setItem('deltaTime', remainedTime);
    }
})

function generateRequestToGetJson(url, method, successFunc, unSuccessFunc) {
    return generatePromiseRequestWithHeader(url, method, successFunc, unSuccessFunc, 'json');
}

async function generatePromiseRequestWithHeader(url, method, successFunc, unSuccessFunc, dataType) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/api' + url,
            method: method,
            dataType: dataType,
            headers: {
                'Authorization': 'Bearer ' + localStorage['accessToken']
            },
            success: function (res) {
                if (typeof successFunc === 'function') {
                    successFunc(res);
                }
                resolve();
            },
            error: function (xhr) {
                onError(xhr, unSuccessFunc, reject);
            }
        })
    })
}

async function generateRequestWithHeaderWithoutPromise(url, method, successFunc, unSuccessFunc) {
    $.ajax({
        url: '/api' + url,
        method: method,
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function (res) {
            successFunc(res);
        },
        error: function (xhr) {
            if (typeof unSuccessFunc === 'function') {
                unSuccessFunc(xhr);
            }
        }
    })
}

function onError(xhr, unSuccessFunc, reject) {
    if (typeof unSuccessFunc === 'function') {
        unSuccessFunc(xhr);
    }
    let contentType = xhr.getResponseHeader('Content-type');
    if (contentType != null && contentType === 'application/json') {
        console.log(xhr.status + ': ' + xhr.statusText);
        let rawResponse = xhr.responseText;
        if (rawResponse !== null && JSON.parse(rawResponse) !== undefined) {
            console.log(JSON.parse(rawResponse)['message']);
        } else {
            console.log(rawResponse);
        }
    }
    reject(xhr.status + ': ' + xhr.statusText);
}

function updateToken() {
    console.log('updating token...')
    $.ajax({
        url: '/api/auth/token',
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage['refreshToken']
        },
        success: function (res) {
            localStorage.setItem('entryTime', new Date().getTime().toString());
            tokens = res;
            localStorage.setItem('refreshToken', tokens['refreshToken']);
            localStorage.setItem('accessToken', tokens['accessToken']);

            setInterval(updateToken, updateTime);
            localStorage.setItem('deltaTime', updateTime.toString());
        }
    })

}