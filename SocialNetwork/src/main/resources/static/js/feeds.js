let currUsername;
let postsPageNumber = 0;
let isPostsLoading = false;
let limitPageHeight = 0.85;

$(document).ready(function () {
    setUsername();
    $('.group-name').click(redirectToGroups);
    updatePostsImages();
    generateRequestToGetJson('/posts/' + currUsername + '?page=' + 0, 'GET', setCounters, null);
});


function updatePostsImages() {
    let posts = $('.post');
    postsPageNumber = 1;
    $.each(posts, function () {
        let index = 0;
        let images = $(this).find('.images').children('.post-img');
        $(this).find('.prev').click(function () {
            images.eq(index).removeClass('active');
            index--;
            if (index < 0) {
                index = images.length - 1
            }
            images.eq(index).addClass('active');
        });
        $(this).find('.next').click(function () {
            images.eq(index).removeClass('active');
            index++;
            if (index >= images.length) {
                index = 0;
            }
            images.eq(index).addClass('active');
        });
    })
}

function setCounters(data) {
    totalPostsPagesCount = data['totalPagesCount'];
    scrollPosts('/posts/' + currUsername + '?page=');
}

function setUsername() {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];
}
