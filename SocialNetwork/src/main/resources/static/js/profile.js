let currUsername;
let profileUsername;
$(document).ready(function () {
    setMetaData();
    $('.bio').click(updateBio);

    $('.logout-button').click(logout);
    $('.image').hover(editProfilePhoto, removeEditButton);
    $('.edit-icon').click(async function () {
        await generatePromiseRequestWithHeader('/users/' + currUsername, 'GET', editProfile, null, 'json');
    });
    if (currUsername !== profileUsername) {
        $('.add-friend-btn').click(processClick);
        $('.chatbtn').click(moveToChat);
    }
});

function setMetaData() {
    let token = localStorage.getItem('refreshToken');
    let payload = token.split('.')[1];
    payload = window.atob(payload);
    payload = JSON.parse(payload);
    currUsername = payload['sub'];
    profileUsername = window.location.href.split('/').filter(Boolean).pop();
}

function processClick(event) {
    let state = event.target.getAttribute('value');
    if (state === 'Accept' || state === 'Add') {
        addFriend(event);
    } else if (state === 'Delete' || state === 'Revoke') {
        deleteFriend(event);
    }
}

function addFriend(event) {
    generateRequestWithHeaderWithoutPromise('/users/' + currUsername + '/friends/' + profileUsername, 'POST',
        function (data) {
            let state = data['state'];
            if (state === '-1') {
                $(event.target).text('Revoke');
            } else if (state === '0') {
                $(event.target).text('Delete');
            }
            $(event.target).off('click');
            $(event.target).click(deleteFriend);
        });
}

function deleteFriend(event) {
    $.ajax({
        url: '/api/users/' + currUsername + '/friends/' + profileUsername,
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function () {
            $(event.target).text('Add');
            $(event.target).off('click');
            $(event.target).click(addFriend);
        }
    })
}

function logout() {
    $.ajax({
        url: '/api/logout',
        method: 'POST',
        contentType: false,
        processData: false,
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function () {
            localStorage.setItem('entryTime', new Date().getTime().toString());
            window.location.href = '/app/login';
        }
    })
}

function editProfilePhoto() {
    let editBtn = $('.image-edit-icon');
    editBtn.off('click');
    editBtn.click(function () {
        $('.accept-buttons').remove();
        showAcceptButtons();
        $('#new-profile-image').click();
    });

    let profileImage = $('#profile-image');
    profileImage.addClass('edit-img');
    editBtn.css('display', 'flex');
}

function removeEditButton() {
    let editBtn = $('.image-edit-icon');
    let profileImage = $('#profile-image');
    profileImage.removeClass('edit-img');
    editBtn.css('display', 'none');
}

function showAcceptButtons() {
    let buttons = $('<div>').addClass('accept-buttons');
    let acceptButton = $('<a>').addClass('accept').html('<span>Accept</span>');
    let cancelButton = $('<a>').addClass('cancel').html('<span>Cancel</span>');
    buttons.append(acceptButton);
    buttons.append(cancelButton);

    cancelButton.click(function () {
        $('.accept-buttons').remove();
    });
    acceptButton.click(function () {
        $('.accept-buttons').remove();
        let file = $('#new-profile-image')[0].files[0];
        let data = new FormData();
        data.append('avatar', file);

        $.ajax({
            url: '/api/users/' + currUsername,
            type: 'PATCH',
            data: data,
            headers: {
                'Authorization': 'Bearer ' + localStorage['accessToken']
            },
            processData: false,
            contentType: false,
            success: function (value) {
                value = sanitize(value);
                $('#profile-image').attr('src', value['avatarLink']);
            }
        });

    });
    $('.profile-side').prepend(buttons);
}


function editProfile(data) {
    let body = $('.body');
    $('.edit-form').remove();
    body.prepend('<div class="body-shadow"></div>');

    let form = $('<form>').addClass('edit-form');
    let nameInput = $('<input>').attr('id', 'name').attr('placeholder', 'Name')
        .attr('autocomplete', 'off').val(data['name']);

    let surnameInput = $('<input>').attr('id', 'surname').attr('placeholder', 'Surname')
        .attr('autocomplete', 'off').val(data['surname']);

    let ageInput = $('<input>').attr('id', 'age').attr('placeholder', 'Age')
        .attr('autocomplete', 'off').val(data['age']);

    let usernameInput = $('<input>').attr('id', 'username').attr('placeholder', 'Username')
        .attr('autocomplete', 'off').val(data['username']);

    let emailInput = $('<input>').attr('id', 'email').attr('placeholder', 'Email').attr('type', 'email')
        .attr('autocomplete', 'off').val(data['email']);

    let phoneInput = $('<input>').attr('id', 'phone')
        .attr('placeholder', 'Phone number')
        .attr('autocomplete', 'off').val(data['phoneNumber']);

    let passwordInput = $('<input>').attr('type', 'password')
        .attr('placeholder', 'Last password').attr('id', 'last-password')
        .attr('autocomplete', 'off').val(data['password']);

    let errors = ('<ul class="err-message update-errors"></ul>');

    let newPasswordInput = $('<input>').attr('type', 'password')
        .attr('placeholder', 'New password').attr('id', 'new-password')
        .attr('autocomplete', 'off');

    let maleRadio = $('<input>').attr('id', 'Male').addClass('toggle toggle-left')
        .attr('name', 'toggle').attr('value', 'false').attr('type', 'radio');
    let maleLabel = $('<label>').attr('for', 'Male').addClass('btn').text('Male');

    let femaleRadio = $('<input>').attr('id', 'Female').addClass('toggle toggle-right')
        .attr('name', 'toggle').attr('value', 'true').attr('type', 'radio');
    let femaleLabel = $('<label>').attr('for', 'Female').addClass('btn').text('Female');

    if (data['gender'] === 'Male') {
        maleRadio.attr('checked', 'checked');
    } else {
        femaleRadio.attr('checked', 'checked');
    }

    let acceptButton = $('<button>').addClass('form-btn sx accept-edit').attr('type', 'submit').text('Accept');
    let cancelButton = $('<button>').addClass('form-btn dx cancel-edit').attr('type', 'button')
        .attr('name', 'button').attr('value', 'reg').attr('id', 'reg').text('Cancel');
    // let errorMessage = $('<ul>').addClass('err-message registration-err');

    cancelButton.click(function () {
        form.fadeOut(250, function () {
            form.remove();
            $('.body-shadow').fadeOut(250);
            $('.body-shadow').remove();
            $('.update-errors').empty();
            $('.update-errors').remove();
        })
    })

    acceptButton.click(async function (event) {
        event.preventDefault();
        $('.update-errors').empty();
        await update(data);
    })

    form.append(nameInput, surnameInput, ageInput, usernameInput, passwordInput, newPasswordInput, emailInput, phoneInput, maleRadio,
        maleLabel, femaleRadio, femaleLabel, acceptButton, cancelButton);


    $('.body-shadow').fadeIn(250, function () {
        body.append(form).fadeIn(250);
        body.append(errors);
    })
}

function update(data) {
    let name = $('#name').val();
    let surname = $('#surname').val();
    let age = $('#age');
    let username = $('#username');
    let lastPassword = $('#last-password');
    let newPassword = $('#new-password');
    let email = $('#email');
    let phone = $('#phone');
    let gender;

    if (isNaN(parseInt(age.val()))) {
        age.css('border-bottom', '1px solid #ff6363');
        age.val('');
        return;
    }

    let male = $('#Male');
    let female = $('#Female');
    if (male.is(':checked')) {
        gender = 'Male';
    } else if (female.is(':checked')) {
        gender = 'Female';
    }

    let updatedData = new FormData();
    updatedData.append('name', name);
    updatedData.append('surname', surname);
    updatedData.append('age', parseInt(age.val()).toString());
    updatedData.append('username', username.val());
    updatedData.append('gender', gender);

    let field = lastPassword.val().trim();
    if (field.length >= 6) {
        updatedData.append('password', lastPassword.val());
    } else if (field.length > 0) {
        lastPassword.css('border-bottom', '1px solid #ff6363');
        lastPassword.val('');
        return;
    }
    field = newPassword.val().trim();
    if (field.length >= 6) {
        updatedData.append('newPassword', field);
    } else if (field.length > 0) {
        newPassword.css('border-bottom', '1px solid #ff6363');
        newPassword.val('');
        return;
    }

    field = email.val().trim();
    if (field.length !== 0) {
        if (field.toLowerCase().match(/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)) {
            updatedData.append('email', field);
        } else {
            email.css('border-bottom', '1px solid #ff6363');
            email.val('');
            return;
        }
    }

    field = phone.val().trim();
    if (field.length !== 0) {
        if (field.match(/\d/g).length === 10) {
            updatedData.append('phoneNumber', field);
        } else {
            phone.css('border-bottom', '1px solid #ff6363');
            phone.val('');
            return;
        }
    }

    $.ajax({
        url: '/api/users/' + currUsername,
        data: updatedData,
        method: 'PATCH',
        contentType: false,
        processData: false,
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + localStorage['accessToken']
        },
        success: function (res) {
            res = sanitize(res);
            updateProfileFromResponse(res);

            $('.edit-form').fadeOut(250, function () {
                $('.edit-form').remove();
                $('.body-shadow').fadeOut(250);
                $('.body-shadow').remove();
                $('.update-errors').empty();
                $('.update-errors').remove();
            })
        },
        error: function (xhr) {
            onEditProfileError(xhr);
        }
    })
}

function onEditProfileError(xhr) {
    let contentType = xhr.getResponseHeader('Content-type');
    if (contentType != null && contentType === 'application/json') {
        let rawResponse = xhr.responseText;
        let response = JSON.parse(rawResponse);
        for (let key in response) {
            if (response[key] !== null && response[key]['message'] !== undefined) {
                response[key] = sanitize(response[key]);
                $('.update-errors').append('<li>' + response[key]['message'] + '</li>');
            }
        }
    }
}

function updateProfileFromResponse(data) {
    currUsername = data['username']
    localStorage.setItem("accessToken", data['tokens']['accessToken']);
    localStorage.setItem("refreshToken", data['tokens']['refreshToken']);
    window.history.replaceState({path: '/app/profile/' + data['username']}, '', '/app/profile/' + data['username']);
    $('.user-name').text(data['name'] + ' ' + data['surname']);
    $('.username').text(data['username']);
    $('.bio-text').text(data['bio']);
    $('.phone').text(data['phoneNumber']);
    $('.email').text(data['email']);
    $('.gender').text(data['gender']);
    $('.age').text(data['age'])
}

function updateBio() {
    if (currUsername === profileUsername) {
        let bio = $('.bio');
        let bioPrev = bio.html();
        let area = $('<textarea>').addClass('post-area bio-area').val($('.bio-text').html()).attr('maxlength', '255');
        let buttons = $('<div>').addClass('bio-buttons');
        let sendBtn = $('<button>').html('&#10003;');
        let cancelBtn = $('<button>').html('&times;');
        area.val();
        buttons.append(sendBtn, cancelBtn);
        $('.profile-nav-info').append(buttons);
        bio.replaceWith(area);

        cancelBtn.click(function () {
            area.replaceWith(bio);
            bio.html(bioPrev);
            buttons.remove();
            bio.click(updateBio);
        });
        sendBtn.click(function () {
            let updatedBio = new FormData();
            updatedBio.append('bio', area.val());

            $.ajax({
                url: '/api/users/' + currUsername,
                method: 'PATCH',
                data: updatedBio,
                contentType: false,
                processData: false,
                dataType: 'json',
                headers: {
                    'Authorization': 'Bearer ' + localStorage['accessToken']
                },
                success: function (res) {
                    res = sanitize(res);
                    area.replaceWith(bio);
                    bio.html(bioPrev);
                    buttons.remove();
                    bio.click(updateBio);
                    $('.bio-text').text(res['bio']);
                }
            })
        })
    }
}

function moveToChat(event) {
    generateRequestWithHeaderWithoutPromise('/chats/personal/' + profileUsername, 'GET',
        function (data) {
            window.location.href = '/app/chats?id=' + data['globalId']['id'];
        },
        function () {
            sendRequestToCreateChat();
        })
}

function sendRequestToCreateChat() {
    generateRequestWithHeaderWithoutPromise('/chats/personal/' + profileUsername, 'POST',
        function (data) {
            window.location.href = '/app/chats?id=' + data['globalId']['id'];
        })
}