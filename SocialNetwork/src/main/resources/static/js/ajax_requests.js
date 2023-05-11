function generateRequestWithHeaderAndFunc(url, method, successFunc) {
    return new Promise(resolve => {
        $.ajax({
            url: url,
            method: method,
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage['accessToken']
            },
            success: function (res) {
                successFunc(res);
                resolve();
            },
        })
    })
}

function generateRequestWithHeaderAndFuncWithoutPromise(url, method, successFunc) {
    $.ajax({
        url: url,
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
