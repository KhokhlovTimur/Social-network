function generateRequestToSendJson(url, method, successFunc, unSuccessFunc) {
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
                let rawResponse = xhr.responseText;
                if (rawResponse !== null) {
                    console.log(JSON.parse(rawResponse)['message']);
                }
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

function generateRequestWithData(url, method, successFunc, dataType, data) {
    $.ajax({
        url: '/api' + url,
        method: method,
        dataType: dataType,
        data: data,
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function (res) {
            successFunc(res);
        },
    })
}
