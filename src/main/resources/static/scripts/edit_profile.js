function save() {
    var city = $('#city').val();
    var school = $('#school').val();
    var rang = $('#rang').val();
    var trener = $('#trener').val();
    var about = $('#about').val();

    $.ajax({
        type: 'POST',
        url: '/api/setUserInfo',
        data: {token: $.cookie('token'), city: city, school: school, rang: rang, trener: trener, about: about},
        success: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
            var token = jqXHR.response;

            window.location.href = './profile.html';
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(1234);

            alert(JSON.parse(jqXHR.responseText).error);
        }
    });

}