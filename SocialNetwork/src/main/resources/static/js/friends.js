let isScrollLoading = false;
let totalPagesCount = 0;
let pageNumber = 1;
let friendsContainer = $('.friends');
let limitPageHeight = 0.9;
let currUsername;
let isGlobalSearchDisabled = false;
let isFriendsLoading = false;

$(document).ready(function () {
    $('html, body').animate({scrollTop: 0}, 'fast');
    setUsername();
    $('.friend-name').click(function () {
        let username = $(this).parent().next('.friend-username').text().substring(1);
        window.location.href = '/app/profile/' + username;
    })

    let outgoing = $('.outgoing');
    let incoming = $('.incoming');
    incoming.click(function () {
        $(this).toggleClass('pressed');
        pressRequestButtons(outgoing, incoming, 'incoming');
    });
    outgoing.click(function () {
        $(this).toggleClass('pressed');
        pressRequestButtons(incoming, outgoing, 'outgoing');
    })
    $('#cbx').change(updateGlobalSearchParameter);

    let searchTimer;
    $('.search_text').keyup(function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            findFriends();
        }, 120)
    });

    $('.friend-message').click(goToChat);

    $('.friend-add').click(addFriend);

    generateRequestWithHeaderWithoutPromise('/users/' + currUsername + '/friends?type=friends&query=&page=' + pageNumber,
        'GET', async function (data) {
            totalPagesCount = data['pagesCount'];
            scrollFriends('/users/' + currUsername + '/friends?type=friends&query=&page=');
        });
})

function goToChat(event) {
    let username = $(event.target).closest('.buttons').closest('.friend-card').find('.friend-username').text().substring(1);
    generateRequestWithHeaderWithoutPromise('/chats/personal/' + username, 'GET',
        function (data) {

            window.location.href = '/app/chats?id=' + data['globalId']['id'];
        },
        function () {
            generateRequestWithHeaderWithoutPromise('/chats/personal/' + username, 'POST',
                function (data) {
                    window.location.href = '/app/chats?id=' + data['globalId']['id'];
                })
        })
}

function addFriend(event) {
    let friendUsername = $(event.target).closest('.buttons').closest('.friend-card').find('.friend-username').text().substring(1);
    generateRequestWithHeaderWithoutPromise('/users/' + currUsername + '/friends/' + friendUsername, 'POST',
        function (data) {
            $(event.target).off('click');
            $(event.target).click(deleteFriend);
            $(event.target).html(getState(data['state']));
        });
}

function deleteFriend(event) {
    let friendUsername = $(event.target).closest('.buttons').closest('.friend-card').find('.friend-username').text().substring(1);
    $.ajax({
        url: '/api' + '/users/' + currUsername + '/friends/' + friendUsername,
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function () {
            $(event.target).off('click');
            $(event.target).click(addFriend);
            $(event.target).addClass('add').html('+ Add');
        }
    })
}

function pressRequestButtons(firstBtn, secondBtn, type) {
    pageNumber = 0;
    friendsContainer.empty();
    $('.search_text').val('');
    $('#cbx').prop('checked', false);
    let url;
    if (firstBtn.hasClass('pressed')) {
        firstBtn.toggleClass('pressed');
    }
    if (secondBtn.hasClass('pressed') && !firstBtn.hasClass('pressed')) {
        url = '/users/' + currUsername + '/friends?type=' + type + '&query=&page=';
        $('#cbx').prop('disabled', true);
        isGlobalSearchDisabled = true;
    } else {
        type = 'friends';
        url = '/users/' + currUsername + '/friends?type=' + type + '&query=&page=';
        $('#cbx').prop('disabled', false);
        isGlobalSearchDisabled = false;
    }
    generateRequestWithHeaderWithoutPromise(url + 0, 'GET',
        function (data) {
            totalPagesCount = data['pagesCount'];
            processFriends(data);
            pageNumber = 1;
            scrollFriends(url);
        });
}

function setUsername() {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];
}

function createFriendCard(data) {
    let card = $('<div>').addClass('friend-card');
    let image = $('<img>').addClass('round').attr('src', data['avatarLink']);
    let name = $('<h3>');
    let nameSpan = $('<span>').addClass('friend-name').html(data['name'] + ' ' + data['surname']);
    nameSpan.click(function () {
        let username = $(this).parent().next('.friend-username').text().substring(1);
        window.location.href = '/app/profile/' + username;
    })
    name.append(nameSpan);

    let username = $('<h6>').addClass('friend-username').html('@' + data['username']);
    let buttons = $('<div>').addClass('buttons');
    let messageBtn = $('<button>').addClass('primary friend-message').html('Chat');
    buttons.append(messageBtn);

    let stateButtonVal;
    let state = data['friendStatus'].toString();
    let clazz;
    let clickFunc;
    let addBtn = $('<button>').addClass('primary ghost friend-add');

    if (state === '0') {
        stateButtonVal = 'Added';
        clazz = 'added';
        clickFunc = function () {
        };
    } else if (state === '-1') {
        stateButtonVal = 'Revoke';
        clazz = 'sent';
        clickFunc = deleteFriend;
    } else if (state === "1") {
        stateButtonVal = 'Accept';
        clazz = 'wait';
        clickFunc = addFriend;
    } else {
        stateButtonVal = '+ Add';
        clazz = 'add';
        clickFunc = addFriend;
    }

    addBtn.addClass(clazz).html(stateButtonVal);
    addBtn.click(clickFunc);

    messageBtn.click(goToChat)

    buttons.append(addBtn);
    card.append(image, name, username, buttons);

    return card;
}

function getState(state) {
    if (state === '-1') {
        return 'Revoke'
    } else if (state === '0') {
        return 'Added';
    }
}

async function findFriends() {
    pageNumber = 0;
    let name = $('.search_text').val().trim();
    let chBox = $('#cbx');
    friendsContainer.empty();

    let type;
    if (chBox.is(':checked') && !isGlobalSearchDisabled) {
        type = 'global';
        console.log('global search')
    } else if ($('.incoming').hasClass('pressed')) {
        type = 'incoming';
        console.log('incoming search')
    } else if ($('.outgoing').hasClass('pressed')) {
        type = 'outgoing';
        console.log('outgoing search')
    } else {
        type = 'friends';
        console.log('friends search')
    }
    let url = '/users/' + currUsername + '/friends?type=' + type + '&query=' + name + '&page=';

    // if (name.length > 0) {
    let isReady = false;
    if (name.length > 0) {
        isReady = true;
    } else if (!isFriendsLoading) {
        isReady = true;
    }

    if (isReady) {
        isFriendsLoading = true
        await generateRequestWithHeaderWithoutPromise(url + pageNumber, 'GET',
            function (data) {
                friendsContainer.empty();
                processFriends(data);
                pageNumber = 1;
                totalPagesCount = data['pagesCount'];
                scrollFriends(url);
            })
    }
}

function scrollFriends(url) {
    $(window).off('scroll');
    $(window).scroll(async function () {
        let scrollHeight = $(window).scrollTop();
        let windowHeight = $(window).height();
        let documentHeight = $(document).height();

        if ((scrollHeight + windowHeight) / documentHeight >= limitPageHeight) {
            if (!isScrollLoading && pageNumber <= totalPagesCount - 1) {
                isScrollLoading = true;
                await generateRequestWithHeaderWithoutPromise(url + pageNumber, 'GET', processFriends);
            }
        }
    });
}

function processFriends(data) {
    totalPagesCount = data['pagesCount'];
    let friends = data['users'];
    for (let friend of friends) {
        friendsContainer.append(createFriendCard(friend));
    }
    pageNumber++;

    isScrollLoading = false;
    isFriendsLoading = false;
}

function updateGlobalSearchParameter() {
    let url;
    pageNumber = 0;
    friendsContainer.empty();
    $('.search_text').val('');
    if ($(this).is(':checked')) {
        url = '/users/' + currUsername + '/friends?type=global&query=&page=';
    } else {
        url = '/users/' + currUsername + '/friends?type=friends&query=&page=';
    }
    generateRequestWithHeaderWithoutPromise(url + pageNumber, 'GET',
        function (data) {
            processFriends(data);
            pageNumber = 1;
            totalPagesCount = data['pagesCount'];
            scrollFriends(url);
        })
}
