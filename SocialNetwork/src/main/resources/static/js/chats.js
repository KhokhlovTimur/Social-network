let messagesBlock = $('.messages-block');
let chatBlock = $('.messages-chat');
let chats = $('#chats-block');
let messages = $('#messages');
let messageInput = $('#messageInput');
let chatDiv = $('.chat-div');
let client;
let searchInput = $('#chat-search');
let currentChat;
let chatType;
let chatId;
let subscriptions = new Set();
let socket;
let chatsMap = new Map();
let currMessage;
let currTimer;
let chatsEndpoint = '/app/chats/';
let brokerEndpoint = '/topic';
let currUsername;
let navbarButton = $('.navbarImage');
let isLoading = false;
let isMessagesLoading = false;
let usersList;


$(document).ready(async function () {
    chats.empty();
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];

    $(window).on('beforeunload', function () {
        disconnectSocket();
    });
    generateRequestWithHeaderWithoutPromise('/chats/all', 'GET', fillLastMessages, null);

    messageInput.keydown(function (e) {
        if (e.code === 'Enter') {
            sendMessage();
            $('.friends-list').addClass('hide');
        }
    });

    $('.footer-chat').click(function () {
        $('.friends-list').addClass('hide');
    })
    let searchTimer;
    searchInput.keyup(function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            chats.empty();
            findChat();
        }, 180)
    });

    $('#name').click(function () {
        if (chatType === 'PERSONAL') {
            window.location.href = '/app/profile/' + $('#name').text();
        }
    });

    $('.plus').click(addChat);

    navbarButton.click(function () {
        if (client) {
            client.disconnect();
        }
    });

    url = window.location.toString();
    if (url.includes('/app/chats?id=') && url.indexOf('?id=') !== -1) {
        chatId = url.substring(url.lastIndexOf('=') + 1, url.length);
        connectToChat();
    }

});

function addChat() {
    history.replaceState(null, null, '/app/chats');
    messagesBlock.hide();
    $('.body').append(createForm());
}

async function createBlockWithFriends() {
    usersList = $('<div>').addClass('friends-list');

    await generateRequestWithHeaderWithoutPromise('/chats/' + chatId + '/' + currUsername + '/friends', 'GET',
        function (data) {
            createFriendsBlock(data);
        });
}

function createForm() {
    let form = $('<form>').attr('action', '/app/chats').attr('method', 'post').addClass('add-chat')
        .attr('enctype', 'multipart/form-data');
    let span = $('<span>').addClass('create-header').text('Create chat');
    let input = $('<input>').attr('type', 'text').attr('id', 'inputText')
        .addClass('name').attr('required', true).attr('name', 'name').attr('autocomplete', 'off');

    let fileLabel = $('<label>').addClass('input-file').attr('required', true);
    let fileInput = $('<input>').attr('type', 'file').attr('name', 'image');
    let fileSpan = $('<span>').addClass('input-file-btn').text('Image');
    let button = $('<button>').attr('type', 'submit').addClass('add-btn').text('Create');

    form.append(span, input);
    fileLabel.append(fileInput);
    fileLabel.append(fileSpan);
    form.append(fileLabel);
    form.append(button);

    return form;
}

function disconnectSocket() {
    $.each(Array.from(subscriptions.values()), function (index, value) {
        value.unsubscribe();
    });
    client.disconnect();
    socket.close();
}

async function fillLastMessages(data) {
    if (data.length > 0) {

        for (let chat of data) {
            chatsMap.set(chat['globalId']['id'].toString(), chat);
            let chatDiv = createChat(chat, chat['globalId']['chatType'] === 'PERSONAL');
            chats.prepend(chatDiv);
            let chatSwitchTimer;
            chatDiv.click(function (event) {
                clearTimeout(chatSwitchTimer);
                chatSwitchTimer = setTimeout(function () {
                    tapOnChat(event);
                }, 180)

            });
        }
    }


    socket = new SockJS('/ws');
    client = Stomp.over(socket);
    client.connect({}, onConnected, onError);
}

function setMessage(data) {
    if (data['type'] === 'MESSAGE') {
        let username = data['sender']['username'];
        if (username !== currUsername) {
            currMessage.innerHTML = '<span class="ch-name">' + username + ': ' + ' </span>' + data['content'];
        } else {
            currMessage.textContent = data['content'];
        }
    } else {
        currMessage.textContent = data['content'];
    }
}


function setTimer(data) {
    currTimer.textContent = convertDate(data['sendingTime']);
}

function convertDate(value) {
    let time = new Date(value);
    let hours = time.getUTCHours() + 3;
    let minutes = time.getUTCMinutes();
    return hours.toString()
            .padStart(2, '0') + ':' +
        minutes.toString()
            .padStart(2, '0');
}

function getChatId(id) {
    let chatId;
    if (id.indexOf('ChatGlobalIdDto(') !== -1) {
        id = id.replace('ChatGlobalIdDto(id=', '');
        chatId = id.substring(0, id.indexOf(','));
    } else {
        id = JSON.parse(id);
        chatId = id['id'];
    }

    return chatId;
}

async function tapOnChat(event) {
    $('.add-chat').remove();
    $('.friends-list').addClass('hide');
    let glId = event.target.closest('.chat-div').getAttribute('value');
    let isCurrChat = false;
    if (glId.indexOf('ChatGlobalIdDto') !== -1) {
        glId = glId.replace('ChatGlobalIdDto(id=', '');
        if (glId.substring(0, glId.indexOf(',')) === chatId) {
            isCurrChat = true;
        }
        chatId = glId.substring(0, glId.indexOf(','));
    } else {
        glId = JSON.parse(glId);
        if (chatId === glId['id']) {
            isCurrChat = true;
        }
        chatId = glId['id'];
    }

    if (!isCurrChat && !isMessagesLoading) {
        connectToChat();
    }
}

function connectToChat() {
    chatBlock.empty();
    generateRequestWithHeaderWithoutPromise('/chats/' + chatId, 'GET', setCurrChat, function () {
        history.replaceState(null, null, '/app/chats');
        isMessagesLoading = false;
    });
}

async function setCurrChat(data) {
    isMessagesLoading = true;
    currentChat = data;
    chatType = currentChat['globalId']['chatType'];
    history.replaceState(null, null, '/app/chats?id=' + chatId);

    if (chatType === 'PUBLIC') {
        $('.members-count').html('+');
    } else {
        $('.members-count').empty();
    }
    createBlockWithFriends();
    usersList.addClass('hide');

    generateRequestWithHeaderWithoutPromise('/chats/' + chatId + "/messages",
        'get', getMessagesFromResponseAndOpenConnection, null)
}


function findChat(event) {
    $('.friends-list').remove();
    if (!isLoading) {
        isLoading = true;
        let payload = searchInput.val().trim();
        if (payload.length > 0) {
            chats.empty();
            generateRequestWithHeaderWithoutPromise('/chats?name=' + payload, 'GET', showChats, null);
        } else {
            chats.empty();
            showChats(Array.from(chatsMap.values()));
            isLoading = false;
        }
    }
}

function showChats(data) {
    $.each(data, function (index, value) {
        let chat;
        let globalId = value['globalId'];

        if (globalId['chatType'] === 'PERSONAL') {
            chat = createChat(value, true)
        } else {
            chat = createChat(value, false);
        }

        let chatSwitchTimer;
        chat.click(function (event) {
            clearTimeout(chatSwitchTimer);
            chatSwitchTimer = setTimeout(function () {
                tapOnChat(event);
            }, 180)
        });

        chats.prepend(chat);
    })
    isLoading = false;

}

function createChat(value, isPersonal) {
    let id = JSON.stringify(value['globalId']);
    let chatDiv = $('<div>').addClass('chat').addClass('chat-div');
    chatDiv.attr('value', id);
    let photoDiv = $('<div>').addClass('photo');

    let imageUrl;
    let chatName;
    let username;
    if (isPersonal) {
        let user = value['secondUser'];
        imageUrl = user['avatarLink'];
        chatName = user['name'] + ' ' + user['surname'];
        username = $('<p>').addClass('username-chat').html('@' + user['username']);
    } else {
        imageUrl = value['imageLink'];
        chatName = value['name'];
    }

    photoDiv.attr('style', 'background-image: url(\"' + imageUrl + '\")');
    chatDiv.append(photoDiv);

    let contactDiv = $('<div>').addClass('desc-contact');
    let nameP = $('<p>').addClass('name');

    nameP.html(chatName);
    contactDiv.append(nameP);
    if (isPersonal) {
        contactDiv.append(username);
    } else {
        contactDiv.css('height', '40px');
    }
    let messageP = $('<p>').addClass('message chat-block');
    contactDiv.append(messageP);
    currMessage = messageP[0];
    setMessage(JSON.parse(id)['lastMessage']);
    chatDiv.append(contactDiv);

    let timerDiv = $('<div>').addClass('timer chat-timer');

    currTimer = timerDiv[0];
    setTimer(JSON.parse(id)['lastMessage']);
    chatDiv.append(timerDiv);

    return chatDiv;
}


function getMessagesFromResponseAndOpenConnection(data) {
    let name = $('#name');
    messagesBlock.show();

    if (chatType === 'PUBLIC') {
        name.text(currentChat['name']);
        $.each(data, function (index, value) {
            createMessageForChat(value);
        });
    } else {
        name.text(currentChat['secondUser']['username']);
        $.each(data, function (index, value) {
            createMessageForChat(value);
        });
    }
    isMessagesLoading = false;
    messagesBlock.show();
}

function onConnected() {
    subscribe();
}

function subscribe() {
    $.each(Array.from(chatsMap.values()), function (index, value) {
        let sub = client.subscribe(brokerEndpoint + "/" + value['globalId']['id'], processMessage);
        subscriptions.add(sub);
    });
}

function processMessage(rawMessage) {
    let newMessage = JSON.parse(rawMessage.body);
    let messages = $('.chat-block');
    let chatDiv;

    $.each(messages, function (index, value) {

        if (getChatId($(this).closest('.chat-div').attr('value')).toString() === newMessage['globalId']['id'].toString()) {
            currMessage = value;
            chatsMap.get(getChatId($(this).closest('.chat-div').attr('value')).toString())['globalId']['lastMessage']['content'] = newMessage['content'];
            setMessage(newMessage);
            chatDiv = $(this).closest('.chat-div');
        }
    });

    $.each($('.chat-timer'), function (index, value) {
        if (getChatId($(this).closest('.chat-div').attr('value')).toString() === newMessage['globalId']['id'].toString()) {
            currTimer = value;
            chatsMap.get(getChatId($(this).closest('.chat-div').attr('value')).toString())['globalId']['lastMessage']['sendingTime'] = newMessage['sendingTime'];
            setTimer(newMessage);
        }
    });

    chatDiv.remove();
    chats.prepend(chatDiv);

    if (chatId !== undefined && newMessage['globalId']['id'].toString() === chatId.toString()) {
        createMessageForChat(newMessage);
    }
}

function sendMessage() {
    let payload = messageInput.val();
    messageInput.val('');
    if (payload.length > 0) {
        if (client) {
            let message = createMessage(payload, 'MESSAGE');
            client.send(chatsEndpoint + chatId + '/send', {'token': localStorage.getItem('accessToken')}, JSON.stringify(message));
        }
    }
}

function createMessage(content, type) {
    return {
        'content': content,
        'type': type
    };
}

function onError(error) {
    console.log('disconnected')
}

function createMessageForChat(value) {
    let message;
    if (value['type'] === 'MESSAGE') {
        if (currUsername === value['sender']['username']) {
            message = rightMessage(value);
            chatBlock.prepend(message);
            chatBlock.scrollTop(chatBlock[0].scrollHeight);
        } else {
            message = leftMessage(value);
            chatBlock.prepend(message);
        }
    }
}


function leftMessage(value) {
    let message = $('<div>').addClass('message');
    let textDiv = $('<div>').addClass('left-message')
    let text = $('<p>').addClass('text');
    let photo = $('<div>').addClass('photo');
    photo.attr('style', 'background-image: url(' + value['sender']['avatarLink'] + ')');
    message.append(photo);

    let styleText = $('<p>').addClass('message-span');
    styleText.html(value['content']);
    let timer = $('<span>').addClass('time');
    timer.html(convertDate(value['sendingTime']));
    text.append(styleText);
    text.append(timer);
    textDiv.append(text);
    message.append(textDiv);
    return message;
}


function createFriendsBlock(data) {
    $('.friends-list').remove();
    let count = $('.members-count');
    count.off('click');
    let btn = $('<button>').addClass('add-friend-chat-btn').text('Add');
    usersList.append(btn);
    btn.click(function () {
        if (chatType === 'PUBLIC') {
            let users = $('.friends-list input[type="checkbox"]:checked');
            let request = new Set();
            users.each(function () {
                let username = $(this).attr('id');
                $(this).closest('.friend').remove();
                request.add(username);
            })

            $.ajax({
                url: '/api/chats/' + chatId,
                method: 'POST',
                data: JSON.stringify({'usernames': [...request]}),
                contentType: 'application/json',
                headers: {
                    'Authorization': 'Bearer ' + localStorage['accessToken']
                },
                success: function () {
                    usersList.addClass('hide');
                },
                error: function () {
                    usersList.addClass('hide');
                }
            })
        }
    })

    if (data !== undefined && data['users'].length > 0) {
        for (let friend of data['users']) {
            createFriend(friend['avatarLink'], friend['username'], friend['username']);
        }
        usersList.addClass('hide');
    }
    messagesBlock.append(usersList);
    $('.members-count').click(function () {
        usersList.toggleClass('hide');
    });
}

function createFriend(photoSrc, id, name) {
    let userDiv = $('<div>').addClass('friend');

    let userPhoto = $('<div>').addClass('friend-photo');
    let userImg = $('<img>').attr('src', photoSrc).attr('alt', 'User Photo');
    userPhoto.append(userImg);
    userDiv.append(userPhoto);
    let checkbox = $('<input>').attr('type', 'checkbox').attr('id', id);
    userDiv.append(checkbox);

    let label = $('<label>').attr('for', id).text(name);
    userDiv.append(label);
    usersList.append(userDiv);
}

function rightMessage(value) {
    let message = $('<div>').addClass('message text-only');
    let text = $('<div>').addClass('my-message');
    let textP = $('<p>').addClass('text');
    let styleText = $('<p>').addClass('message-span');
    styleText.html(value['content']);

    let timer = $('<span>').addClass('my-message-time');
    timer.html(convertDate(value['sendingTime']));
    textP.append(styleText);
    textP.append(timer);
    text.append(textP);
    message.append(text);

    return message;
}