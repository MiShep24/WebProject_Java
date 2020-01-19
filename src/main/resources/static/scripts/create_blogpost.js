$("#photo").live('change', function (event) {
    var title = $('#title').val();
    var body = $('#body').val();

    if (title.trim() === '' || body.trim === '') {
        alert("Не введены данные");
    } else {

        var fileList = event.target.files;
        var formData = fileList[0];

        var reader = new FileReader();
        var fileByteArray = [];
        reader.readAsArrayBuffer(formData);
        reader.onloadend = function (evt) {
            if (evt.target.readyState == FileReader.DONE) {
                var arrayBuffer = evt.target.result,
                    array = new Uint8Array(arrayBuffer);
                for (var i = 0; i < array.length; i++) {
                    fileByteArray.push(array[i]);
                }
                $.ajax({
                    url: '/api/addBlogPost',
                    type: 'PUT',
                    data: '{"token":"' + $.cookie('token') + '","title":"' + title + '","post_body":"' + body + '", "formData": [' + fileByteArray + ']}',
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (jqXHR, textStatus, errorThrown) {
                        window.location.href = './blog_post.html'
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert("Ошибка");
                    }

                });
            }
        }

    }


});