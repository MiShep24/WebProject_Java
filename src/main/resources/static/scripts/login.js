var token = $.cookie('token');
if (token === undefined) console.log("token not found");
else {
    $.ajax({
        type: 'GET',
        url: '/api/checkToken',
        data: {token: $.cookie('token')},
        success: function (jqXHR, textStatus, errorThrown) {
            var token = jqXHR.response;
            if (token !== "false") window.location.href = './profile.html';

        }
    });
}

$.ajaxSetup({async: true});

function login() {
    var emailuser = $('#emailuser').val();
    var passworduser = $('#passworduser').val();

    $.ajax({
        type: 'POST',
        url: '/api/login',
        data: {emailuser: emailuser, passworduser: passworduser},
        success: function (jqXHR, textStatus, errorThrown) {
            var token = jqXHR.response;
            $.cookie('token', token);
            window.location.href = './profile.html';
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(JSON.parse(jqXHR.responseText).error);
        }
    });

}