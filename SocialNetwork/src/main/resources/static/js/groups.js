let group = $('.group');
let groups = $('#groupsList');
let main = $('.main');
let groupId;
let groupInfo = $('#groupInfo');
let usersPageNumber = 0;
let pageNumber = 0;
// let totalPostsPagesCount;
let totalGroupsPagesCount;
// let posts = $('.posts');
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
// let likeButton = $('.like-button');
let limitPageHeight = 0.95;
let isLoading = false;

$(document).ready(function () {
    url = window.location.toString();
    setUsername();

    $('.add-group-btn').click(addGroup);
    if (url.includes('/app/groups?id=') && url.indexOf('?id=') !== -1) {
        groupId = url.substring(url.lastIndexOf('=') + 1, url.length);
        sendRequestToGetGroup();
    } else {
        main.css('display', 'flex');
        groups.css('display', 'flex');
    }

    generateRequestToSendJson('/users/' + currUsername + '/groups?page=0', 'GET', updateGroupsSet);
    scrollGroups('/users/' + currUsername + '/groups?page=');

    // likeButton.click();
    group.click(getGroupIdFromEvent);
    joinButton.click(joinToGroup);
    searchInput.keyup(findGroup);

    // $('.like-button').click(putLike);
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
            createGroup(value).hide().prependTo(groups).slideDown();
        },
        error: function (xhr) {
            console.log(data)
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

// async function putLike(event) {
//     let postId = event.target.closest('.post').getAttribute('value');
//     currBtn = $(event.target);
//     if (!currBtn.hasClass('liked')) {
//         await generatePromiseRequestWithHeader('/groups/ + ' + groupId + '/posts/' + postId + '/likes', 'POST',
//             null, null, 'text');
//     } else {
//         await generatePromiseRequestWithHeader('/groups/ + ' + groupId + '/posts/' + postId + '/likes', 'DELETE',
//             null, null, 'text');
//     }
//
//     currBtn.toggleClass('liked');
//
//     await generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + postId + '/likes/count', 'GET',
//         setLikesCount, null, 'json');
// }
//
// function setLikesCount(data) {
//     currBtn.next('.count').html(data);
// }

// let currCount;
// let isLikePut;
let deletePostId;

// async function createPost(value) {
//     let post = $('<div>').addClass('post');
//     post.attr('value', value['id']);
//     let inner = $('<div>').addClass('post-inner');
//     if (isCurrUserOwner) {
//         let menuBtn = $('<div>').addClass('menu-btn');
//
//         let menu = createPostMenu(value['id']);
//         menuBtn.html('...');
//         menuBtn.click(function () {
//             menu.toggleClass('visible');
//         });
//         inner.append(menuBtn);
//         inner.append(menu);
//     }
//
//     let textDiv = $('<div>').addClass('post-text');
//     let groupName = $('<p>').addClass('group-name');
//     groupName.html(value['group']['name']);
//
//     let imagesDiv = createImagesDiv(value);
//
//     let author = $('<p>').addClass('post-author');
//     // author.html(value['author']['username']);
//     let text = $('<div>').addClass('post-description');
//     text.html(value['text']);
//
//     let footer = $('<div>').addClass('post-footer');
//     let time = $('<span>').addClass('time');
//     time.html(convertDate(value['dateOfPublication']));
//
//     let likes = $('<div>').addClass('likes');
//
//     await generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + value['id'] + '/likes/' + currUsername,
//         'GET', setLikeStatus);
//     let likesBtn = $('<button>').addClass('like-button');
//     if (isLikePut) {
//         likesBtn.addClass('liked');
//     }
//
//     likesBtn.click(putLike);
//     let count = $('<span>').addClass('count');
//     await generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + value['id'] + '/likes/count', 'GET',
//         setLikesCountToBlock, null, 'json');
//
//     count.html(currCount);
//     likes.append(likesBtn);
//     likes.append(count);
//     footer.append(time);
//     footer.append(likes);
//
//     textDiv.append(groupName);
//     textDiv.append(imagesDiv);
//     // textDiv.append(author);
//     textDiv.append(text);
//     textDiv.append(footer);
//     inner.append(textDiv);
//     post.append(inner);
//     return post;
// }
//
// function deletePost() {
//     let post = $('.post').filter(function () {
//         return $(this).attr('value') === deletePostId;
//     });
//     post.fadeOut(250, function () {
//         post.remove();
//     });
// }

// function createImagesDiv(value) {
//     let imagesDiv = $('<div>').addClass('images');
//     let files = value['files'];
//     let imagesCount = 0;
//     for (let file of files) {
//         if (file['mimeType'] !== null && file['mimeType'].startsWith('image/')) {
//             let img = $('<img>').addClass('post-img');
//             img.attr('src', file['fileLink']);
//             imagesDiv.append(img);
//             imagesCount++;
//         }
//     }
//
//     let images = imagesDiv.children('.post-img');
//     let index = 0;
//     images.eq(index).addClass('active');
//     if (imagesCount > 1) {
//         let prev = $('<div>').addClass('prev').html('&lt;');
//         let next = $('<div>').addClass('next').html('&gt;');
//         imagesDiv.append(prev);
//         imagesDiv.append(next);
//
//         prev.click(function () {
//             images.eq(index).removeClass('active');
//             index--;
//             if (index < 0) {
//                 index = imagesCount - 1;
//             }
//             images.eq(index).addClass('active');
//         });
//
//         next.click(function () {
//             images.eq(index).removeClass('active');
//             index++;
//             if (index >= imagesCount) {
//                 index = 0;
//             }
//             images.eq(index).addClass('active');
//         });
//     }
//
//     return imagesDiv;
// }

// function createPostMenu(postId) {
//     let menu = $('<ul>').addClass('menu');
//     let deleteBtn = $('<li>').addClass('delete').html('Delete');
//     deleteBtn.click(function (event) {
//         menu.toggleClass('visible');
//         deletePostId = event.target.closest('.post').getAttribute('value');
//         generatePromiseRequestWithHeader('/groups/' + groupId + '/posts/' + deletePostId, 'DELETE', deletePost);
//     });
//
//     let editBtn = $('<li>').addClass('edit').html('Edit');
//     editBtn.click(function () {
//         menu.toggleClass('visible');
//         let description = editBtn.closest('.post[value=' + postId + ']').find('.post-description');
//         let descriptionText;
//         let postArea = description.find('.update-area');
//         if (postArea.length > 0) {
//             descriptionText = postArea.val();
//         } else {
//             descriptionText = description.text();
//         }
//
//         let textarea = $('<textarea>').addClass('post-area update-area');
//         let buttons = $('<div>').addClass('buttons');
//         let sendBtn = $('<button>').html('&#10003;');
//         let cancelBtn = $('<button>').html('&times;');
//         buttons.append(sendBtn);
//         buttons.append(cancelBtn);
//         description.addClass('update-description');
//         cancelBtn.click(function () {
//             description.removeClass('update-description');
//             description.html(descriptionText);
//         });
//
//         sendBtn.click(function () {
//                 let updatedPost = {
//                     'text': textarea.val(),
//                     'files': null
//                 };
//                 console.log(updatedPost)
//
//                 $.ajax({
//                     url: '/api/groups/' + groupId + '/posts/' + postId,
//                     method: 'PUT',
//                     data: JSON.stringify(updatedPost),
//                     contentType: 'application/json',
//                     headers: {
//                         'Authorization': 'Bearer ' + localStorage['accessToken']
//                     },
//                     success: function (res) {
//                         description.html(res['text']);
//                     }
//                 })
//
//                 description.removeClass('update-description');
//             }
//         );
//
//         textarea.html(descriptionText);
//         description.html(textarea);
//         description.append(buttons);
//     })
//     menu.append(editBtn);
//     menu.append(deleteBtn);
//     return menu;
// }

// function setLikeStatus(status) {
//     isLikePut = status;
// }
//
// function setLikesCountToBlock(count) {
//     currCount = count;
// }

async function findGroup() {
    groups.empty();
    let name = searchInput.val().toString().trim();
    name = encodeURIComponent(name);
    isLoading = false;
    if (name.length > 0) {
        pageNumber = 0;
        console.log('NAME > 0')
        generateRequestWithHeaderAndFuncWithoutPromise('/groups?name=' + name + '&page=' + pageNumber, 'GET', processGroups);
        scrollGroups('/groups?name=' + name + '&page=');

    } else if (typeof lastUpdate === 'undefined' || (new Date() - lastUpdate) > updateEvery) {
        lastUpdate = new Date();
        console.log(213)
        await generateRequestToSendJson('/users/' + currUsername + '/groups?page=0', 'GET', updateGroupsSet);

        for (let group of groupsSet) {
            groups.append(createGroup(group));
        }
        scrollGroups('/users/' + currUsername + '/groups?page=');

    } else {
        pageNumber = 1;
        console.log(groupsSet)
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
            if (!isLoading && pageNumber <= totalGroupsPagesCount - 1) {
                isLoading = true;
                generateRequestWithHeaderAndFuncWithoutPromise(url + pageNumber, 'GET', processGroups);
            }
        }
    });
}

function updateGroupsSet(data) {
    totalGroupsPagesCount = data['pagesCount'];
    pageNumber = 1;
    groupsSet.clear();
    for (let group of data['groups']) {
        groupsSet.add(group);
    }
}

function processGroups(data) {
    totalGroupsPagesCount = data['pagesCount'];
    pageNumber++;
    for (let value of data['groups']) {
        groups.append(createGroup(value));
    }
}

function createGroup(value) {
    console.log(value)
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
    await generateRequestToSendJson('/groups/' + groupId + '/users/' + currUsername, 'GET', checkCurrUser);
}

function getGroupIdFromEvent(event) {

    groupId = event.target.closest('.group').getAttribute('value');
    sendRequestToGetGroup();
}

function sendRequestToGetGroup() {
    setUrl();
    posts.empty();
    setGroupMetadata().then(r =>
        generateRequestWithHeaderAndFuncWithoutPromise('/groups/' + groupId, 'GET', showGroup, processError));
}

function processError() {
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
        pageNumber = 0;
        groups.empty();
        console.log('BACK');
        $('#profile-img').removeClass('edit-img');
        $('.profile-edit-buttons').remove();
        $('.image-edit-icon').remove();
        $('.edit-icon').remove();
        $('html, body').animate({scrollTop: 0}, 'slow');
        $(window).off('scroll');
        searchInput.val('');
        findGroup();
        usersPageNumber = 0;
        pageNumber = 0;
    }
}


function showGroup(data) {
    main.hide();
    groupInfo.css('display', 'flex');
    $('html, body').animate({scrollTop: 0}, 'slow');
    generateProfile(data);

    generateRequestWithHeaderAndFuncWithoutPromise('/groups/' + groupId + '/posts?page=' + pageNumber, 'GET', processPosts);
    $(window).scroll(async function () {

        let scrollHeight = $(window).scrollTop(); //dynamic: window + scroll
        let windowHeight = $(window).height(); //const: window
        let documentHeight = $(document).height(); //const: window + scroll space


        if ((scrollHeight + windowHeight) / documentHeight >= limitPageHeight) {
            if (!isLoading && pageNumber <= totalPostsPagesCount - 1) {
                isLoading = true;
                await generateRequestWithHeaderAndFuncWithoutPromise('/groups/' + groupId + '/posts?page=' + pageNumber, 'GET', processPosts);
            }
        }
    });
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

    if (data['creator'] !== null && data['creator']['username'].toString() === currUsername) {
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
    let descrArea = $('<textarea>').addClass('post-area update-area');
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
            data.append('image', $('#new-image')[0].files[0]);

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
                    console.log(res)
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

    let text = $('.post-area');
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
            appendNewPost(res);
        }
    })
}

async function appendNewPost(data) {
    $('.new-post').slideUp(300);
    let addPostBtn = $('.add-post');
    addPostBtn.slideDown();
    await createPost(data).then((post) => addPostBtn.after(post.hide().fadeIn(1000)));
}

function checkCurrUser(isExists) {
    if (isExists) {
        setJoinButtonValue('joined');
    } else {
        setJoinButtonValue('join');
    }
}

// async function setMembersCount() {
//     await generateRequestToSendJson('/groups/' + groupId + '/users?page=' + usersPageNumber, 'GET', setCount);
// }
//
// function setCount(data) {
//     pagesCount = data['pagesCount'];
//     membersCount = data['totalCount'];
// }

// async function processPosts(data) {
//     pageNumber++;
//     let postsData = data['posts'];
//     totalPostsPagesCount = data['totalPagesCount'];
//
//     for (let post of postsData) {
//         await createPost(post).then((data) => posts.append(data));
//     }
//
//     isLoading = false;
// }

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
        await generateRequestToSendJson('/groups/' + groupId + '/users', 'POST');
        await setMembersCount();
        setJoinButtonValue('joined');
    } else {
        await generatePromiseRequestWithHeader('/groups/' + groupId + '/users', 'DELETE', 'text');
        await setMembersCount();
        setJoinButtonValue('join');
    }
    await generateRequestToSendJson('/groups?name=&page=0', 'GET', updateGroupsSet);
    $('.members').html('members: ' + membersCount);
}

// function convertDate(timestamp) {
//     let time = new Date(timestamp);
//     let hours = time.getUTCHours() + 3;
//     let day = time.toLocaleString("en-US", {day: "numeric"});
//     let month = time.toLocaleString("en-US", {month: "short"});
//
//     let minutes = time.getUTCMinutes();
//     return hours.toString()
//             .padStart(2, '0') + ':' +
//         minutes.toString()
//             .padStart(2, '0') + ', ' + day.toString()
//             .padStart(2, '0') + ' ' + month.toString();
// }
