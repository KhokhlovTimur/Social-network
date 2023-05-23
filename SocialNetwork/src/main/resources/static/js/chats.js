let messagesBlock = $('.messages-block');
let chatBlock = $('.messages-chat');
let chats = $('#chats-block');
let messages = $('#messages');
let messageInput = $('#messageInput');
let chatDiv = $('.chat-div');
let client;
let searchInput = $('#chat-search');
let currentChat;
let prevChat;
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


$(document).ready(async function () {
    $(window).on('beforeunload', function () {
        disconnectSocket();
    });
    generateRequestWithHeaderAndFuncWithoutPromise('/chats/all', 'GET', fillLastMessages, null);

    chatDiv.click(tapOnChat);

    searchInput.keyup(findChat);

    messageInput.keydown(function (e) {
        if (e.code === 'Enter') {
            sendMessage();
        }
    });

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

function disconnectSocket() {
    $.each(Array.from(subscriptions.values()), function (index, value) {
        value.unsubscribe();
    });
    client.disconnect();
    socket.close();
}

async function fillLastMessages(data) {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];

    for (let chat of data) {
        chatsMap.set(chat['globalId']['id'].toString(), chat);
    }

    let messages = $('.chat-block');
    for (let val of messages) {
        const chatId = getChatId(val.closest('.chat-div').getAttribute('value'));
        val.closest('.chat-div').setAttribute('value', JSON.stringify(chatsMap.get(chatId)['globalId']));
        let username = chatsMap.get(chatId)['globalId']['lastMessage']['sender']['username'];
        if (username !== currUsername) {
            currMessage = val;
            setMessage(chatsMap.get(chatId)['globalId']['lastMessage'])
        }
    }

    let timers = $('.chat-timer');
    for (let val of timers) {
        const chatId = getChatId(val.closest('.chat-div').getAttribute('value')).toString();
        currTimer = val;
        setTimer(chatsMap.get(chatId)['globalId']['lastMessage']);
    }

    socket = new SockJS('/ws');
    client = Stomp.over(socket);
    client.connect({}, onConnected, onError);
}

function setMessage(data) {
    let username = data['sender']['username'];
    if (username !== currUsername) {
        currMessage.innerHTML = '<span class="ch-name">' + username + ': ' + ' </span>' + data['content'];
    } else {
        currMessage.textContent = data['content'];
    }
}

function setTimer(data) {
    // console.log(data)
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

function tapOnChat(event) {
    prevChat = currentChat;
    let glId = event.target.closest('.chat-div').getAttribute('value');
    if (glId.indexOf('ChatGlobalIdDto') !== -1) {
        glId = glId.replace('ChatGlobalIdDto(id=', '');
        chatId = glId.substring(0, glId.indexOf(','));
    } else {
        glId = JSON.parse(glId);
        chatId = glId['id'];
    }

    connectToChat();
}

function connectToChat() {
    chatBlock.empty();
    generateRequestWithHeaderAndFuncWithoutPromise('/chats/' + chatId, 'GET', setCurrChat, null);
}

function setCurrChat(data) {
    currentChat = data;
    chatType = currentChat['globalId']['chatType'];
    history.replaceState(null, null, '/app/chats?id=' + chatId);

    generateRequestWithHeaderAndFuncWithoutPromise('/chats/' + chatId + "/messages",
        'get', getMessagesFromResponseAndOpenConnection, null)
}


function findChat(event) {
    let payload = searchInput.val().trim();
    if (payload.length > 0) {
        chats.empty();
        generateRequestWithHeaderAndFuncWithoutPromise('/chats?name=' + payload, 'GET', showChats, null);
    } else {
        chats.empty();
        showChats(Array.from(chatsMap.values()));
    }

}

function showChats(data) {
    $.each(data, function (index, value) {
        let chat;
        let globalId = value['globalId'];

        if (globalId['chatType'] === 'PERSONAL') {
            chat = createChat(value['secondUser']['avatarLink'], value['secondUser']['username'], JSON.stringify(globalId));
        } else {
            chat = createChat(value['imageLink'], value['name'], JSON.stringify(globalId));
        }

        chat.click(tapOnChat);
        chats.append(chat);
    })
}

function createChat(imageUrl, name, id) {
    let chatDiv = $('<div>').addClass('chat').addClass('chat-div');
    chatDiv.attr('value', id);
    let photoDiv = $('<div>').addClass('photo');
    photoDiv.attr('style', 'background-image: url(\"' + imageUrl + '\")');
    chatDiv.append(photoDiv);

    let contactDiv = $('<div>').addClass('desc-contact');
    let nameP = $('<p>').addClass('name');
    nameP.html(name);
    contactDiv.append(nameP);

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

    $.each(messages, function (index, value) {

        if (getChatId($(this).closest('.chat-div').attr('value')).toString() === newMessage['globalId']['id'].toString()) {
            currMessage = value;
            chatsMap.get(getChatId($(this).closest('.chat-div').attr('value')).toString())['globalId']['lastMessage']['content'] = newMessage['content'];
            setMessage(newMessage);
        }
    });

    $.each($('.chat-timer'), function (index, value) {
        if (getChatId($(this).closest('.chat-div').attr('value')).toString() === newMessage['globalId']['id'].toString()) {
            currTimer = value;
            chatsMap.get(getChatId($(this).closest('.chat-div').attr('value')).toString())['globalId']['lastMessage']['sendingTime'] = newMessage['sendingTime'];
            setTimer(newMessage);
        }
    });

    if (newMessage['globalId']['id'].toString() === chatId.toString()) {
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
    if (currUsername === value['sender']['username']) {
        message = rightMessage(value);
        chatBlock.prepend(message);
        chatBlock.scrollTop(chatBlock[0].scrollHeight);
    } else {
        message = leftMessage(value);
        chatBlock.prepend(message);
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