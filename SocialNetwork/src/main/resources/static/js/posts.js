let posts = $('.posts');
let totalPostsPagesCount = 0;
let isCurrUserOwner = false;
let likeButton = $('.like-button');

$(document).ready(function () {
    likeButton.click(putLike);
});

let currCount;
let isLikePut;

async function putLike(event) {
    let postId = event.target.closest('.post').getAttribute('value');
    let groupId = event.target.closest('.post-inner').getAttribute('value');
    currBtn = $(event.target);
    if (!currBtn.hasClass('liked')) {
        await generatePromiseRequestWithHeader('/groups/ + ' + groupId + '/posts/' + postId + '/likes', 'POST',
            null, null, 'text');
    } else {
        await generatePromiseRequestWithHeader('/groups/ + ' + groupId + '/posts/' + postId + '/likes', 'DELETE',
            null, null, 'text');
    }

    currBtn.toggleClass('liked');

    await generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + postId + '/likes/count', 'GET',
        setLikesCount, null, 'json');
}

function setLikesCount(data) {
    currBtn.next('.count').html(data);
}

async function createPost(value, isFeeds) {
    let post = $('<div>').addClass('post');
    post.attr('value', value['id']);
    let inner = $('<div>').addClass('post-inner');
    if (value['group'] !== null) {
        inner.attr('value', value['group']['id'])
    }
    else {
        inner.css('background-color', '#faefef');
    }
    if (isCurrUserOwner) {
        let menuBtn = $('<div>').addClass('menu-btn');

        let menu = createPostMenu(value['id']);
        menuBtn.html('...');
        menuBtn.click(function () {
            menu.toggleClass('visible');
        });
        inner.append(menuBtn);
        inner.append(menu);
    }

    let textDiv = $('<div>').addClass('post-text');
    let groupName = $('<p>').addClass('group-name');
    if (value['group'] !== null) {
        groupName.html(value['group']['name']);
        if (isFeeds) {
            groupName.click(redirectToGroups);
        }
    } else {
        groupName.html('News*').css('color', '#000000').css('cursor', 'default');
    }
    let imagesDiv = createImagesDiv(value);

    let text = $('<div>').addClass('post-description');
    text.html(value['text']);

    let footer = $('<div>').addClass('post-footer');
    let time = $('<span>').addClass('time');
    time.html(convertDate(value['dateOfPublication']));

    footer.append(time);

    if (value['group'] !== null) {
        let likes = $('<div>').addClass('likes');
        let likesBtn = $('<button>').addClass('like-button');
        likesBtn.click(putLike);
        let count = $('<span>').addClass('count');
        if (value['isLikedByUser']) {
            likesBtn.addClass('liked');
        }

        count.html(value['likesCount']);
        likes.append(likesBtn);
        likes.append(count);
        footer.append(likes);
    }

    textDiv.append(groupName);
    textDiv.append(imagesDiv);
    // textDiv.append(author);
    textDiv.append(text);
    textDiv.append(footer);
    inner.append(textDiv);
    post.append(inner);
    return post;
}

function deletePost() {
    let post = $('.post').filter(function () {
        return $(this).attr('value') === deletePostId;
    });
    post.fadeOut(250, function () {
        post.remove();
    });
}

function createImagesDiv(value) {
    let imagesDiv = $('<div>').addClass('images');
    let files = value['files'];
    let imagesCount = 0;
    for (let file of files) {
        if (file['mimeType'] !== null && file['mimeType'].startsWith('image/')) {
            let img = $('<img>').addClass('post-img');
            img.attr('src', file['fileLink']);
            imagesDiv.append(img);
            imagesCount++;
        }
    }

    let images = imagesDiv.children('.post-img');
    let index = 0;
    images.eq(index).addClass('active');
    if (imagesCount > 1) {
        let prev = $('<div>').addClass('prev').html('&lt;');
        let next = $('<div>').addClass('next').html('&gt;');
        imagesDiv.append(prev);
        imagesDiv.append(next);

        prev.click(function () {
            images.eq(index).removeClass('active');
            index--;
            if (index < 0) {
                index = imagesCount - 1;
            }
            images.eq(index).addClass('active');
        });

        next.click(function () {
            images.eq(index).removeClass('active');
            index++;
            if (index >= imagesCount) {
                index = 0;
            }
            images.eq(index).addClass('active');
        });
    }

    return imagesDiv;
}


function createPostMenu(postId) {
    let menu = $('<ul>').addClass('menu');
    let deleteBtn = $('<li>').addClass('delete').html('Delete');
    deleteBtn.click(function (event) {
        menu.toggleClass('visible');
        deletePostId = event.target.closest('.post').getAttribute('value');
        generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + deletePostId, 'DELETE', deletePost);
    });

    let editBtn = $('<li>').addClass('edit').html('Edit');
    editBtn.click(function () {
        menu.toggleClass('visible');
        let description = editBtn.closest('.post[value=' + postId + ']').find('.post-description');
        let descriptionText;
        let postArea = description.find('.update-area');
        if (postArea.length > 0) {
            descriptionText = postArea.val();
        } else {
            descriptionText = description.text();
        }

        let textarea = $('<textarea>').addClass('post-area update-area');
        let buttons = $('<div>').addClass('buttons');
        let sendBtn = $('<button>').html('&#10003;');
        let cancelBtn = $('<button>').html('&times;');
        buttons.append(sendBtn);
        buttons.append(cancelBtn);
        description.addClass('update-description');
        cancelBtn.click(function () {
            description.removeClass('update-description');
            description.html(descriptionText);
        });

        sendBtn.click(function () {
                let updatedPost = {
                    'text': textarea.val(),
                    'files': null
                };

                $.ajax({
                    url: '/api/groups/' + groupId + '/posts/' + postId,
                    method: 'PUT',
                    data: JSON.stringify(updatedPost),
                    contentType: 'application/json',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage['accessToken']
                    },
                    success: function (res) {
                        res = sanitize(res);
                        description.html(res['text']);
                    }
                })

                description.removeClass('update-description');
            }
        );

        textarea.html(descriptionText);
        description.html(textarea);
        description.append(buttons);
    })
    menu.append(editBtn);
    menu.append(deleteBtn);
    return menu;
}


function setLikeStatus(status) {
    isLikePut = status;
}

function setLikesCountToBlock(count) {
    currCount = count;
}

async function setMembersCount() {
    await generateRequestToGetJson('/groups/' + groupId + '/users?page=' + usersPageNumber, 'GET', setCount);
}

function setCount(data) {
    pagesCount = data['pagesCount'];
    membersCount = data['totalCount'];
}

async function processPosts(data) {
    let postsData = data['posts'];
    totalPostsPagesCount = data['totalPagesCount'];

    for (let post of postsData) {
        await createPost(post, true).then((data) => posts.append(data));
    }
    postsPageNumber++;
    isPostsLoading = false;
}

function convertDate(timestamp) {
    let time = new Date(timestamp);
    let hours = time.getUTCHours() + 3;
    let day = time.toLocaleString("ru-RU", {day: "numeric"});
    let month = time.toLocaleString("ru-RU", {month: "long"});
    month = month.slice(0, -1) + "я";

    let minutes = time.getUTCMinutes();
    return hours.toString()
            .padStart(2, '0') + ':' +
        minutes.toString()
            .padStart(2, '0') + ', ' + day.toString()
            .padStart(2, '0') + ' ' + month.toString();
}

function scrollPosts(url) {
    $(window).scroll(async function () {

        let scrollHeight = $(window).scrollTop(); //dynamic: window + scroll
        let windowHeight = $(window).height(); //const: window
        let documentHeight = $(document).height(); //const: window + scroll space

        if ((scrollHeight + windowHeight) / documentHeight >= limitPageHeight) {
            if (!isPostsLoading && postsPageNumber <= totalPostsPagesCount - 1) {
                isPostsLoading = true;
                await generateRequestWithHeaderWithoutPromise(url + postsPageNumber, 'GET', processPosts);
            }
        }
    });
}

function redirectToGroups(event) {
    let groupId = event.target.closest('.post-inner').getAttribute('value');
    if (groupId !== '-1') {
        window.location.href = '/app/groups?id=' + groupId;
    }
    else {
        $(event.target).css('cursor', 'default');
    }
}