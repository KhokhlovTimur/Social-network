function generateRequestToGetJson(url, method, successFunc, unSuccessFunc) {
    return generatePromiseRequestWithHeader(url, method, successFunc, unSuccessFunc, 'json');
}

function generatePromiseRequestWithHeader(url, method, successFunc, unSuccessFunc, dataType) {
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
            error: function (xhr, status, error) {
                if (typeof unSuccessFunc === 'function') {
                    unSuccessFunc(xhr);
                }
                console.log(xhr.status + ': ' + xhr.statusText);
                reject(xhr.status + ': ' + xhr.statusText);

            }
        })
    })
}

function generateRequestWithHeaderAndFuncWithoutPromise(url, method, successFunc) {
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
    })
}
