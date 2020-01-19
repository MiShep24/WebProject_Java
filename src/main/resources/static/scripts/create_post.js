function send() {
    var name = $('#name').val();
    var age = $('#age').val();
    var date = $('#date').val();
    var location = $('#location').val();
    var level = $('#level').val();
    var info = $('#info').val();

    $.ajax({
        type: 'POST',
        url: '/api/addPost',
        data: {
            token: $.cookie('token'),
            name: name,
            age: age,
            date: date,
            location: location,
            level: level,
            info: info
        },
        success: function (jqXHR, textStatus, errorThrown) {

            window.location.href = './profile.html';
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(JSON.parse(jqXHR.responseText).error);
        }
    });
}