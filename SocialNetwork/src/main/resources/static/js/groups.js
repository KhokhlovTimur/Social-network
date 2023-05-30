let group = $('.group');
let groups = $('#groupsList');
let main = $('.main');
let groupId;
let groupInfo = $('#groupInfo');
let usersPageNumber = 0;
let groupsPageNumber = 0;
let totalGroupsPagesCount;
let url;
let membersCount;
let joinButton = $('.join-button');
let pagesCount = 0;
let currUsername;
let searchInput = $('.search_text');
let groupsSet = new Set();
let lastUpdate;
let updateEvery = 2500;
let currBtn;
let limitPageHeight = 0.85;
let isPostsLoading = false;
let isGroupsLoading = false;

$(document).ready(function () {
    url = window.location.toString();
    setUsername();

    likeButton.click(putLike);

    $('.add-group-btn').click(addGroup);
    if (url.includes('/app/groups?id=') && url.indexOf('?id=') !== -1) {
        groupId = url.substring(url.lastIndexOf('=') + 1, url.length);
        if (groupId === null || groupId === 'null'){
            processError();
        }
        sendRequestToGetGroup();
    } else {
        main.css('display', 'flex');
        groups.css('display', 'flex');
        generateRequestToGetJson('/users/' + currUsername + '/groups?page=0', 'GET', updateGroupsSet);
        scrollGroups('/users/' + currUsername + '/groups?page=');
    }

    group.click(getGroupIdFromEvent);
    joinButton.click(joinToGroup);
    let searchTimer;
    searchInput.keyup(function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            findGroup();
        }, 170)
    });
});

function addGroup() {
    let shadow = $('.body-shadow');
    let form = $('.add-group');
    shadow.fadeIn(500, function () {
        form.fadeIn(300);
    });

    $('.close-button').click(function () {
        form.fadeOut(400, function () {
            shadow.fadeOut(600);
            $('.error').hide();
        })
    })

    $('#addGroupForm').submit(async function (event) {
        event.preventDefault();
        await sendGroup();
    });
}

async function sendGroup() {
    $('.error').hide();
    let name = $('#new-group-name');

    let description = $('#new-group-description');
    let image = $('#new-group-image')[0].files[0];
    $('#new-group-image').val('');

    let data = new FormData();
    data.append('name', name.val());
    data.append('description', description.val());
    data.append('image', image);
    description.val('');
    name.val('');

    await $.ajax({
        url: '/api/groups',
        method: 'POST',
        data: data,
        contentType: false,
        processData: false,
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function (value) {
            $('#addGroupForm').fadeOut(400, function () {
                $('.body-shadow').fadeOut(600);
            })
            value = sanitize(value);
            createGroup(value).hide().prependTo(groups).slideDown();
        },
        error: function (xhr) {
            let json = xhr.responseText;
            let contentType = xhr.getResponseHeader('Content-type');
            if (contentType != null && contentType === 'application/json') {
                $('.error').css('display', 'flex');
                console.log(xhr.status + ': ' + xhr.statusText);
                $('.error').html(JSON.parse(json)['message']);
            }
        }
    })
}

let deletePostId;

async function findGroup() {
    groups.empty();
    let name = searchInput.val().toString().trim();
    name = encodeURIComponent(name);
    isGroupsLoading = false;
    if (name.length > 0) {
        groupsPageNumber = 0;
        generateRequestWithHeaderWithoutPromise('/groups?name=' + name + '&page=' + groupsPageNumber, 'GET', processGroups);
        scrollGroups('/groups?name=' + name + '&page=');

    } else if (typeof lastUpdate === 'undefined' || (new Date() - lastUpdate) > updateEvery) {
        lastUpdate = new Date();
        await generateRequestToGetJson('/users/' + currUsername + '/groups?page=0', 'GET', updateGroupsSet);

        for (let group of groupsSet) {
            groups.append(createGroup(group));
        }
        scrollGroups('/users/' + currUsername + '/groups?page=');

    } else {
        groupsPageNumber = 1;
        for (let group of groupsSet) {
            groups.append(createGroup(group));
        }
        scrollGroups('/users/' + currUsername + '/groups?page=');
    }
}

function scrollGroups(url, method) {
    $(window).off('scroll');

    $(window).scroll(async function () {

        let scrollHeight = $(window).scrollTop(); //dynamic: window + scroll
        let windowHeight = $(window).height(); //const: window
        let documentHeight = $(document).height(); //const: window + scroll space

        if ((scrollHeight + windowHeight) / documentHeight >= limitPageHeight) {
            if (!isGroupsLoading && groupsPageNumber <= totalGroupsPagesCount - 1) {
                isGroupsLoading = true;
                generateRequestWithHeaderWithoutPromise(url + groupsPageNumber, 'GET', processGroups);
            }
        }
    });
}

function updateGroupsSet(data) {
    totalGroupsPagesCount = data['pagesCount'];
    groupsPageNumber = 1;
    groupsSet.clear();
    for (let group of data['groups']) {
        groupsSet.add(group);
    }
}

function processGroups(data) {
    totalGroupsPagesCount = data['pagesCount'];
    groupsPageNumber++;
    for (let value of data['groups']) {
        groups.append(createGroup(value));
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
    return group;
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
    postsPageNumber = 0;
    groupsPageNumber = 0;
    usersPageNumber = 0;
    posts.empty();
    setGroupMetadata().then(r =>
        generateRequestWithHeaderWithoutPromise('/groups/' + groupId, 'GET', showGroup, processError));
}

function processError() {
    postsPageNumber = 0;
    window.history.replaceState({path: '/app/groups'}, '', '/app/groups');
    history.back();
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
        groups.empty();
        $('#profile-img').removeClass('edit-img');
        $('.profile-edit-buttons').remove();
        $('.image-edit-icon').remove();
        $('.edit-icon').remove();
        $('html, body').animate({scrollTop: 0}, 'slow');
        $(window).off('scroll');
        searchInput.val('');
        findGroup();
        usersPageNumber = 0;
        postsPageNumber = 0;
        groupsPageNumber = 0;
        location.reload();
        isPostsLoading = false;
        isGroupsLoading = false;
    }
}

async function showGroup(data) {
    main.hide();
    groupInfo.css('display', 'flex');
    $('html, body').animate({scrollTop: 0}, 'slow');
    generateProfile(data);
    isPostsLoading = false;
    isGroupsLoading = false;

    await generateRequestWithHeaderWithoutPromise('/groups/' + groupId + '/posts?page=' + postsPageNumber, 'GET', processPosts);
    scrollPosts('/groups/' + groupId + '/posts?page=');
}

function generateProfile(data) {
    if (data['creator'] !== null && data['creator']['username'].toString() === currUsername) {
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
        $('.add-post').show();
        $('.group-profile-info').prepend('<span class="edit-icon">&#9998;</span>');
        $('.edit-icon').show().click(editGroup);
        isCurrUserOwner = true;
    } else {
        $('.add-post').hide();
        isCurrUserOwner = false;
    }

    usersPageNumber = 0;
    $('#profile-img').attr('src', data['imageLink']);
    $('.name').html(data['name']);

    $('.members').html('members: ' + membersCount)
    $('#group-description').html(data['description']);
}

function editGroup() {
    let name = $('.name');
    let description = $('#group-description');
    let lastName = name.text();
    let lastDescr = description.text();
    $('#profile-img').addClass('edit-img');
    let editPhoto = $('<span>').addClass('image-edit-icon').html('+');
    editPhoto.css('display', 'flex');
    $('.group-profile-image').prepend(editPhoto);
    let editProfileIcon = $('.edit-icon');
    editProfileIcon.hide();

    editPhoto.click(function () {
        $('#new-image').click();
    });

    let inputName = $('<input>').addClass('name edit-name');
    inputName.val(lastName);
    let descrArea = $('<textarea>').addClass('post-area update-area').attr('maxlength', '255');
    descrArea.val(lastDescr);

    name.html(inputName);
    description.html(descrArea);

    let buttons = $('<div>').addClass('profile-edit-buttons');
    let sendBtn = $('<button>').html('&#10003;  ');
    let cancelBtn = $('<button>').html('&times;');
    buttons.append(sendBtn);
    buttons.append(cancelBtn);
    $('.group-profile-info').append(buttons);

    cancelBtn.click(function () {
        name.html(lastName);
        description.html(lastDescr);
        editPhoto.hide();
        editProfileIcon.show();
        description.removeClass('update-description');
        $('#profile-img').removeClass('edit-img');
        buttons.remove();
        name.removeClass('edit-name');
        inputName.remove();
    });

    sendBtn.click(function () {
            let data = new FormData();
            data.append('name', inputName.val());
            data.append('description', descrArea.val());
            let file = $('#new-image')[0].files[0];
            if (file !== undefined) {
                data.append('image', file);
            }

            editPhoto.hide();
            editProfileIcon.show();
            description.removeClass('update-description');
            buttons.remove();
            $('#profile-img').removeClass('edit-img');
            name.removeClass('edit-name');
            inputName.remove();

            $.ajax({
                url: '/api/groups/' + groupId,
                data: data,
                method: 'PATCH',
                contentType: false,
                processData: false,
                headers: {
                    'Authorization': 'Bearer ' + localStorage['accessToken']
                },
                success: function (res) {
                    res = sanitize(res);
                    name.html(res['name']);
                    description.text(res['description']);
                    $('#profile-img').attr('src', res['imageLink']);
                },
                error: function () {
                    name.html(lastName);
                    description.text(lastDescr);
                }
            })
        }
    );
}

function generateFieldToAddPost() {
    let newPost = $('<div>').addClass('new-post');
    let exitBtn = $('<span>').addClass('exit-btn');
    let text = $('<textarea>').addClass('post-area new-post-area');
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
        sendNewPost();
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

    let text = $('.new-post-area');
    data.append('text', text.val());

    text.val('');
    $('.files').val('');

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
            res = sanitize(res);
            appendNewPost(res);
        }
    })
    $('.files-list').empty();
    $('.file-input').val('');
    $('.file-input').empty();
    allFiles = [];
}

async function appendNewPost(data) {
    $('.new-post').slideUp(300);
    let addPostBtn = $('.add-post');
    addPostBtn.slideDown();
    await createPost(data, false).then((post) => addPostBtn.after(post.hide().fadeIn(1000)));
}

function checkCurrUser(isExists) {
    if (isExists) {
        setJoinButtonValue('joined');
    } else {
        setJoinButtonValue('join');
    }
}

function setJoinButtonValue(value) {
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

