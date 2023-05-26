let currUsername;
let postsPageNumber = 0;
$(document).ready(function () {
    setUsername();
    $('.group-name').click(redirectToGroups);
    generateRequestToGetJson('/posts/' + currUsername + '?page=' + postsPageNumber, 'GET', updatePostsImages, null);
});

function redirectToGroups(event) {
    let groupId = event.target.closest('.post').getAttribute('value');
    window.location.href = '/app/groups?id=' + groupId;
}

function showP() {

}

let postsMap = new Map();

function updatePostsImages(data) {
    setPostsMap(data['posts']);
    let posts = $('.post');
    $.each(posts, function (value) {
        let id = $(value).closest('.post').getAttribute('value');
        $(value).find('.time').val(postsMap.get(id)['dateOfPublication']);
    })
}


function setPostsMap(data) {
    for (let post of data) {
        postsMap.set(post['id'], post);
    }
}


function setUsername() {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];
}