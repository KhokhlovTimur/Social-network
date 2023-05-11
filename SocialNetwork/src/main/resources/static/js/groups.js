let group = $('.group');
let groups = $('#groupsList');
let groupId;
let groupInfo = $('#groupInfo');
let pageNumber = 0;
let totalCountPages;
let posts = $('#posts');
let url;
let bckButton = $('#backGroups');

$(document).ready(function () {
    url = window.location.toString();
    if (url.includes('/app/groups?id=') && url.indexOf('?id=') !== -1) {
        groupId = url.substring(url.lastIndexOf('=') + 1, url.length);
        sendRequestToGetGroup();
    }
    else {
        groups.removeClass('d-none');
    }
    group.click(getGroupIdFromEvent);
    bckButton.click(backToGroups);
});

function getGroupIdFromEvent(event) {
    groupId = event.target.getAttribute('value');
    history.replaceState(null, null, '/app/groups?id=' + groupId);
    groups.addClass('d-none');
    sendRequestToGetGroup();
}

function sendRequestToGetGroup() {
    generateRequestWithHeaderAndFunc('/api/groups/' + groupId, 'GET', showGroup);
}

function showGroup(data) {
    bckButton.removeClass('d-none');

    let groupInfoLine = $('#groupLine');
    groupInfo.removeClass('d-none');

    groupInfoLine.append('<p class="card-text">' + data['name'] + '</p>');
    groupInfoLine.append('<p class="card-text">' + data['description'] + '</p>');

    generateRequestWithHeaderAndFunc('/api/groups/' + groupId + '/posts?page=' + pageNumber, 'GET', processPosts);
}

function processPosts(data) {
    let postsData = data['posts'];
    totalCountPages = data['totalPagesCount'];

    posts.removeClass('d-none');
    $.each(postsData, function (index, value) {
        posts.append('<div class="row border m-4 bg-light shadow">' +
            '<p>' + value['text'] + '</p>' +
            '</div>')
    })
}

function backToGroups(){
    history.replaceState(null, null, '/app/groups');
    groupInfo.addClass('d-none');
    $('#groupLine').empty();

    posts.addClass('d-none');
    posts.empty();

    groups.removeClass('d-none');
    bckButton.addClass('d-none');

}