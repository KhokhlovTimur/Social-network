$(document).ready(function () {
   $('.postGroup').click(redirectToGroups);
});

function redirectToGroups(event){
    let groupId = event.target.getAttribute('value');
    window.location.href = '/app/groups?id=' + groupId;
}