let group = $('.group');
let groups = $('#groupsList');
let main = $('.main');
let groupId;
let groupInfo = $('#groupInfo');
let pageNumber = 0;
let findPageNumber = 0;
let totalCountPages;
let posts = $('.posts');
let url;
let membersCount;
let joinButton = $('.join-button');
let pagesCount = 0;
let currUsername;
let searchInput = $('.search_text');
let groupsSet = new Set();
let lastUpdate;
let updateEvery = 2500;
let pageSize;
let currBtn;
let likeButton = $('.like-button');

$(document).ready(function () {
    url = window.location.toString();
    setUsername();
    // await

    if (url.includes('/app/groups?id=') && url.indexOf('?id=') !== -1) {
        groupId = url.substring(url.lastIndexOf('=') + 1, url.length);
        sendRequestToGetGroup();
    } else {
        main.css('display', 'flex');
        groups.css('display', 'flex');

    }
    likeButton.click();
    group.click(getGroupIdFromEvent);
    joinButton.click(joinToGroup);
    searchInput.keyup(findGroup);

    $('.like-button').click(putLike);
    // $('.vertical').click(function () {
    //    groups.toggleClass('block');
    // });
});

async function putLike(event) {
    let postId = event.target.closest('.post').getAttribute('value');
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

let currCount;
let isLikePut;
let imgsBlock;
let index = 0;
let deletePostId;

async function createPost(value) {
    let post = $('<div>').addClass('post');
    post.attr('value', value['id']);
    let inner = $('<div>').addClass('post-inner');
    if (isCurrUserOwner) {
        let menuBtn = $('<div>').addClass('menu-btn');
        let menu = $('<ul>').addClass('menu');
        let deleteBtn = $('<li>').addClass('delete').html('Delete');
        let editBtn = $('<li>').addClass('edit').html('Edit');
        menu.append(editBtn);
        menu.append(deleteBtn);
        menuBtn.html('...');
        menuBtn.click(function () {
            menu.toggleClass('visible');
        });
        deleteBtn.click(function (event) {
            deletePostId = event.target.closest('.post').getAttribute('value');
            generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + deletePostId, 'DELETE', deletePost);
        });
        inner.append(menuBtn);
        inner.append(menu);
    }

    let textDiv = $('<div>').addClass('post-text');
    let groupName = $('<p>').addClass('group-name');
    groupName.html(value['group']['name']);

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

    imgsBlock = imagesDiv.children('.post-img');
    imgsBlock.eq(index).addClass('active');

    if (imagesCount > 1) {
        let prev = $('<div>').addClass('prev');
        prev.html('&lt;');
        let next = $('<div>').addClass('next');
        next.html('&gt;');
        imagesDiv.append(prev);
        imagesDiv.append(next);
        prev.click(showPreviousImage);
        next.click(showNextImage);
    }

    let author = $('<p>').addClass('post-author');
    author.html(value['author']['username']);
    let text = $('<div>').addClass('post-description');
    text.html(value['text']);

    let footer = $('<div>').addClass('blog-footer');
    let time = $('<span>').addClass('time');
    time.html(convertDate(value['dateOfPublication']));

    let likes = $('<div>').addClass('likes');

    await generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + value['id'] + '/likes/' + currUsername,
        'GET', setLikeStatus);
    let likesBtn = $('<button>').addClass('like-button');
    if (isLikePut) {
        likesBtn.addClass('liked');
    }

    likesBtn.click(putLike);
    let count = $('<span>').addClass('count');
    await generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + value['id'] + '/likes/count', 'GET',
        setLikesCountToBlock, null, 'json');

    count.html(currCount);
    likes.append(likesBtn);
    likes.append(count);
    footer.append(time);
    footer.append(likes);

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
    let post = $('.delete').filter(function() {
        return $(this).attr('value') === deletePostId;
    });
    console.log(post)
    post.fadeOut();
    post.remove();
}

function showPreviousImage() {
    console.log(1)
    imgsBlock.eq(index).removeClass('active');
    index--;
    if (index < 0) {
        index = imgsBlock.length - 1;
    }
    imgsBlock.eq(index).addClass('active');
}

function showNextImage() {
    console.log(2)
    imgsBlock.eq(index).removeClass('active');
    index++;
    if (index >= imgsBlock.length) {
        index = 0;
    }
    imgsBlock.eq(index).addClass('active');
}


function setLikeStatus(status) {
    isLikePut = status;
}

function setLikesCountToBlock(count) {
    currCount = count;
}

async function findGroup() {
    groups.empty();
    let name = searchInput.val().trim();
    if (name.length > 0) {
        generateRequestWithHeaderAndFuncWithoutPromise('/groups?name=' + name + '&page=' + findPageNumber, 'GET', processGroups);
    } else if (typeof lastUpdate === 'undefined' || (new Date() - lastUpdate) > updateEvery) {
        lastUpdate = new Date();
        await generateRequestToGetJson('/users/' + currUsername + '/groups?page=0', 'GET', updateGroupsSet);
        for (let group of groupsSet) {
            createGroup(group);
        }
    } else {
        for (let group of groupsSet) {
            createGroup(group);
        }
    }
}

function updateGroupsSet(data) {
    groupsSet.clear();
    console.log(data)
    for (let group of data['groups']) {
        groupsSet.add(group);
    }
}

function processGroups(data) {
    pageSize = data['groups'].length;
    for (let value of data['groups']) {
        createGroup(value);
    }
}

function createGroup(value) {
    let group = $('<div>').addClass('card group');
    group.attr('value', value['id']);
    let img = $('<img>').attr('src', value['imageLink']);
    group.append(img);
    group.click(getGroupIdFromEvent);

    let content = $('<div>').addClass('card-content');
    let name = $('<h2>').html(value['name']);
    let description = $('<p>').html(value['description']);
    content.append(name);
    content.append(description);
    group.append(content);

    groups.append(group);
}

function setUsername() {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];
}

async function setGroupMetadata() {
    await setMembersCount();
    await generateRequestToGetJson('/groups/' + groupId + '/users/' + currUsername, 'GET', checkCurrUser);
}

function getGroupIdFromEvent(event) {

    groupId = event.target.closest('.group').getAttribute('value');
    sendRequestToGetGroup();
}

function sendRequestToGetGroup() {
    setUrl();
    posts.empty();
    setGroupMetadata().then(r =>
        generateRequestWithHeaderAndFuncWithoutPromise('/groups/' + groupId, 'GET', showGroup));
}

function setUrl() {
    let currUrl = '/app/groups';
    let url = '/app/groups?id=' + groupId;
    window.history.pushState({path: url}, '', url);
    window.onpopstate = function () {
        window.history.replaceState({path: currUrl}, '', currUrl);
        groupInfo.hide();
        main.css('display', 'flex');
        groups.css('display', 'flex');
        posts.empty();
    }
}

function showGroup(data) {
    main.hide();
    groupInfo.css('display', 'flex');

    generateProfile(data);

    generateRequestWithHeaderAndFuncWithoutPromise('/groups/' + groupId + '/posts?page=' + pageNumber, 'GET', processPosts);
}

let isCurrUserOwner = false;

function generateProfile(data) {
    let addPost = $('<div>').addClass('add-post');
    addPost.html('<span>add post</span>');
    posts.append(addPost);

    addPost.click(function () {
        $(this).fadeOut(500);
        let newPostBlock = $('.new-post');
        if (newPostBlock.length === 0) {
            generateFieldToAddPost(addPost).hide().prependTo(posts).slideDown();
        } else {
            $('.new-post').slideDown(250);
        }
    });

    if (data['creator']['username'].toString() === currUsername) {
        $('.add-post').show();
        isCurrUserOwner = true;
    } else {
        $('.add-post').hide();
        isCurrUserOwner = false;
    }

    pageNumber = 0;
    $('#profile-img').attr('src', data['imageLink']);
    $('.name').html(data['name']);

    $('.members').html('members: ' + membersCount);
    $('#group-description').html(data['description']);
}

function generateFieldToAddPost() {
    let newPost = $('<div>').addClass('new-post');
    let exitBtn = $('<span>').addClass('exit-btn');
    let text = $('<textarea>').addClass('post-area');
    let acceptBtn = $('<button>').addClass('accept-btn');

    acceptBtn.html('Publish');

    text.on('input', function () {
        $(this).css('height', 'auto');
        $(this).css('height', this.scrollHeight + 'px');
        if (this.scrollHeight > 200) {
            text.css('overflow-y', 'scroll');
        } else {
            text.css('overflow-y', 'hidden');
        }
    });
    acceptBtn.click(function () {
        $('.files-list').empty();
        $('.file-input').val('');
        $('.file-input').empty();
        sendNewPost();
        allFiles = [];
    });

    exitBtn.html('&#10006;');
    newPost.append(createFilesUploadMenu);
    newPost.append(text);
    newPost.append(exitBtn);
    newPost.append(acceptBtn);

    exitBtn.click(function () {
        $(newPost).slideUp(250);
        $('.add-post').slideDown();
        $('.files-list').empty();
        $('.file-input').val('');
        allFiles = [];
    });
    return newPost;
}

let allFiles;

function createFilesUploadMenu() {
    let filesDiv = $('<div>').addClass('files-block');
    let fileLabel = $('<label>').addClass('file-label');
    fileLabel.attr('for', 'file-input');
    fileLabel.html('<span class="file-text">Выберите файл</span>');
    let file = $('<input>').addClass('file-input');
    file.attr('id', 'file-input');
    file.attr('type', 'file');
    file.attr('multiple', 'multiple');
    let files = $('<div>').addClass('files-list');
    allFiles = [];

    file.on('change', function () {
        let fileList = $('.files-list');
        let files = Array.from(this.files);
        files.forEach(function (file) {
            allFiles.push(file);
            let listItem = $('<div class="file-list-item"></div>');
            let fileName = $('<span>').text(file.name);
            let removeButton = $('<span class="delete-button">&#10006;</span>');

            removeButton.on('click', function () {
                $(this).parent('.file-list-item').remove();
            });

            listItem.append(fileName, removeButton);
            fileList.append(listItem);
        });
    });

    filesDiv.append(fileLabel);
    filesDiv.append(file);
    filesDiv.append(files);

    return filesDiv;
}

function sendNewPost() {
    let postFiles = allFiles;
    let data = new FormData();
    for (let file of postFiles) {
        data.append('files', file);
    }

    data.append('text', $('.post-area').val());
    $('.post-area').val('');
    $('.files')

    $.ajax({
        url: '/api/groups/' + groupId + '/posts',
        method: 'POST',
        data: data,
        contentType: false,
        processData: false,
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function (res) {
            appendNewPost(res);
        }
    })
}

function appendNewPost(data) {
    $('.new-post').slideUp(300);
    let addPostBtn = $('.add-post');
    addPostBtn.slideDown();
    createPost(data).then((post) => addPostBtn.after(post.hide().fadeIn(1000)));
}

function checkCurrUser(isExists) {
    if (isExists) {
        setJoinButtonValue('joined');
    } else {
        setJoinButtonValue('join');
    }
}

async function setMembersCount() {
    await generateRequestToGetJson('/groups/' + groupId + '/users?page=' + pageNumber, 'GET', setCount);
}

function setCount(data) {
    pagesCount = data['pagesCount'];
    membersCount = data['totalCount'];
}

async function processPosts(data) {
    let postsData = data['posts'];
    totalCountPages = data['totalPagesCount'];

    for (let post of postsData) {
        await createPost(post).then((data) => posts.append(data));
    }
}

function setJoinButtonValue(value) {
    console.log(value)
    if (value === 'join') {
        joinButton.removeClass('subscribed');
    } else {
        joinButton.addClass('subscribed');
    }
    joinButton.html(value);
}

async function joinToGroup() {
    if (!joinButton.hasClass('subscribed')) {
        await generateRequestToGetJson('/groups/' + groupId + '/users', 'POST');
        await setMembersCount();
        setJoinButtonValue('joined');
    } else {
        await generatePromiseRequestWithHeader('/groups/' + groupId + '/users', 'DELETE', 'text');
        await setMembersCount();
        setJoinButtonValue('join');
    }
    await generateRequestToGetJson('/groups?name=&page=0', 'GET', updateGroupsSet);
    $('.members').html('members: ' + membersCount);
}

function convertDate(timestamp) {
    let time = new Date(timestamp);
    let hours = time.getUTCHours() + 3;
    let day = time.toLocaleString("en-US", {day: "numeric"});
    let month = time.toLocaleString("en-US", {month: "short"});

    let minutes = time.getUTCMinutes();
    return hours.toString()
            .padStart(2, '0') + ':' +
        minutes.toString()
            .padStart(2, '0') + ', ' + day.toString()
            .padStart(2, '0') + ' ' + month.toString();
}
