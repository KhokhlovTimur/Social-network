let isLoading = false;
let totalPagesCount = 0;
let pageNumber = 0;
let friendsContainer = $('.friends');
let limitPageHeight = 0.9;
let currUsername;

$(document).ready(function () {
    $('html, body').animate({scrollTop: 0}, 'fast');
    setUsername();
    $('.friend-name').click(function () {
        let username = $(this).parent().next('.friend-username').text().substring(1);
        console.log(username)
        window.location.href = '/app/profile/' + username;
    })

    $('.search_text').keyup(findFriends);
    scrollFriends('/users/' + currUsername + '/friends?type=friends&page=');
})

function setUsername() {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];
}

function createFriendCard(data) {
    let card = $('<div>').addClass('friend-card').attr('value', data['username']);
    let image = $('<img>').addClass('round').attr('src', data['avatarLink']);
    let name = $('<h3>');
    let nameSpan = $('<span>').addClass('friend-name').val(data['name'] + ' ' + data['surname']);
    name.append(nameSpan);

    let username = $('<h6>').addClass('friend-username').val('@' + data['username']);
    let buttons = $('<div>').addClass('buttons');
    let messageBtn = $('<button>').addClass('primary').html('Message');
    let addBtn = $('<button>').addClass('primary ghost').html('Add');

    addBtn.click(function () {
    })

    messageBtn.click(function () {

    })

    buttons.append(messageBtn, addBtn);
    card.append(image, name, username, buttons);

    return card;
}

function findFriends() {
    $(window).off('scroll');
    pageNumber = 0;
    let name = $('.search_text').val();
    console.log($('#cbx'));
    if ($('#cbx').is(':checked')) {
        let url = '/users/' + currUsername + '/friends?type=global&page=';
        scrollFriends(url);
        console.log('Checked');
    } else {
    }

    // generatePromiseRequestWithHeader('/');
}

async function scrollFriends(url) {
    pageNumber = 0;
    $(window).off('scroll');
    await generateRequestWithHeaderWithoutPromise(url + pageNumber, 'GET', processFriends);
    $(window).scroll(async function () {
        console.log('scroll')
        let scrollHeight = $(window).scrollTop();
        let windowHeight = $(window).height();
        let documentHeight = $(document).height();

        if ((scrollHeight + windowHeight) / documentHeight >= limitPageHeight) {
            console.log(213)
            if (!isLoading && pageNumber <= totalPagesCount - 1) {
                isLoading = true;
                console.log(isLoading)
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

    isLoading = false;
}