let image;

$(document).ready(function () {
    $('.remove-image').click(removeUpload);
    $('.file-upload-btn').click(function () {
        $('.file-upload-input').click();
    });

    $('.file-upload-input').change(function () {
        readURL(this);
    });
    $('.file-generate-btn').click(generateImage);
})

function readURL(input) {
    if (input.files && input.files[0]) {
        let reader = new FileReader();
        reader.onload = function (e) {
            $('.image-upload-wrap').hide();
            $('.file-upload-image').attr('src', e.target.result);
            $('.file-upload-content').show();
            $('.image-title').html(input.files[0].name);
        };

        reader.readAsDataURL(input.files[0]);

    } else {
        removeUpload();
    }
}

function removeUpload() {
    $('.file-upload-input').replaceWith($('.file-upload-input').clone());
    $('.file-upload-content').hide();
    $('.image-upload-wrap').show();
}

$('.image-upload-wrap').bind('dragover', function () {
    $('.image-upload-wrap').addClass('image-dropping');
});
$('.image-upload-wrap').bind('dragleave', function () {
    $('.image-upload-wrap').removeClass('image-dropping');
});

function generateImage(event) {
    event.preventDefault();
    let image = $('.file-upload-input')[0].files[0];
    let data = new FormData();
    data.append('text', $('.post-area').val());

    fetch('/app/files/random?query=city')
        .then(res => res.blob())
        .then(blob => {
            image = blob;
            data.append('image', image);
            $.ajax({
                url: '/app/admin/posts',
                method: 'POST',
                data: data,
                contentType: false,
                processData: false,
                headers: {
                    'Authorization': 'Bearer ' + localStorage['accessToken']
                },
                success: function (value) {
                    window.location.href = '/app/feeds'
                },
            })
        });
}
