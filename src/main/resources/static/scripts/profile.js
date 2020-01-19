const token = $.cookie('token');
if (token === undefined) {
    window.location.href = './login.html';
} else {
    $.ajax({
        type: 'GET',
        url: '/api/checkToken',
        data: {token: $.cookie('token')},
        success: function (jqXHR, textStatus, errorThrown) {
            const token = jqXHR.response;
            console.log(token);
            if (token === "false") window.location.href = './login.html';
            else load();
        }
    });
}

var url_string = window.location.href; //window.location.href
var url = new URL(url_string);
const user_id = url.searchParams.get("user_id");


function load() {


    $.ajax({
        type: 'GET',
        url: '/api/getUser',
        data: user_id === null ? {token: $.cookie('token')} : {token: $.cookie('token'), uid: user_id},
        success: function (jqXHR, textStatus, errorThrown) {
            if(user_id !== null){
                $("#divButton").hide();
                $("#changedivPhoto").hide();
            }

            $("#fontFIO").text(jqXHR.lastName + " " + jqXHR.firstName + " " + jqXHR.middleName);
            $("#bdate").text(jqXHR.birthday);
            $(".photoAvatar").attr('src', jqXHR.avatar);
            //if(jqXHR.userInfo == undefined)

            if (jqXHR.userInfo !== undefined) {
                $("#city").text(jqXHR.userInfo.city);
                $("#school").text(jqXHR.userInfo.school);
                $("#rang").text(jqXHR.userInfo.rang);
                $("#trener").text(jqXHR.userInfo.trener);
                $("#about").text(jqXHR.userInfo.about);
            }
            //if(token === "false") window.location.href = './login.html';
        }
    });


    $.ajax({
        type: 'POST',
        url: '/api/getPosts',
        data: user_id === null ? {token: $.cookie('token')} : {token: $.cookie('token'), uid: user_id},
        success: function (jqXHR, textStatus, errorThrown) {
            var token = jqXHR.response;

            for (var i = jqXHR.length - 1; i >= 0; i--) {
                console.log(jqXHR[i]);

                var post = jqXHR[i];

                var resp = ``;

                if (post.isMember) {
                    resp = `<div class="divPost">
                        <table>
                            <tr>
                                <th colspan="2"><h2 class="fontFIO">` + post.name + `</h2><!-- Название соревнований--></th>
                            </tr>
                            <tr>
                                <th><h3>Возраст участников:</h3></th>
                                <th>` + post.age + `</th>
                            </tr>
                            <tr>
                                <th><h3>Дата проведения:</h3></th>
                                <th>` + post.date + `</th>
                            </tr>
                            <tr>
                                <th><h3>Место проведения:</h3></th>
                                <th>` + post.location + `</th>
                            </tr>
                            <tr>
                                <th><h3>Уровень соревнований:</h3></th>
                                <th>` + post.level + `</th>
                            </tr>
                            <tr>
                                <th><h3>Дополнительная информация:</h3></th>
                                <th>` + post.info + `</th>
                            </tr>
                        </table>
                         <button class="sendRequest">Заявка подана</button>
                        <div class="members"><a href="./list_members.html?pid=` + post.pid + `">Все участники</a></div>
                        <div>
                            <div class="divLike">
                                <a id="link_` + post.pid + `" href="#like" onclick="
                                    $.ajax({
                                        type : 'POST',
                                        url : '/api/changeLikeToPost',           
                                        data: { token: $.cookie('token'), pid: ` + post.pid + `},
                                        success:function (jqXHR, textStatus, errorThrown) {
                                            document.getElementById('link_` + post.pid + `').textContent = 'LIKE ' + jqXHR.response;

                                        }
                                    });
                                ">LIKE ` + post.likes_count + `</a>                            </div>
                            <div class="divComment">
                                <a href="blog_comments.html?post_id=` + post.pid + `&isPost=true">COMMENT</a>
                            </div>
                        </div>
                    </div>`
                } else {
                    resp = `<div class="divPost">
                        <table>
                            <tr>
                                <th colspan="2"><h2 class="fontFIO">` + post.name + `</h2><!-- Название соревнований--></th>
                            </tr>
                            <tr>
                                <th><h3>Возраст участников:</h3></th>
                                <th>` + post.age + `</th>
                            </tr>
                            <tr>
                                <th><h3>Дата проведения:</h3></th>
                                <th>` + post.date + `</th>
                            </tr>
                            <tr>
                                <th><h3>Место проведения:</h3></th>
                                <th>` + post.location + `</th>
                            </tr>
                            <tr>
                                <th><h3>Уровень соревнований:</h3></th>
                                <th>` + post.level + `</th>
                            </tr>
                            <tr>
                                <th><h3>Дополнительная информация:</h3></th>
                                <th>` + post.info + `</th>
                            </tr>
                        </table>
                        <button class="sendRequest" onclick="
                                   $.ajax({
                                        type : 'POST',
                                        url : '/api/addMember',           
                                        data: { token: $.cookie('token'), pid: ` + post.pid + `},
                                        success:function (jqXHR, textStatus, errorThrown) {
                                            document.location.reload(true);
                                        }
                                    });
                        "> + Подать заявку</button>
                        <div class="members"><a href="./list_members.html?pid=` + post.pid + `">Все участники</a></div>
                        <div>
                            <div class="divLike">
                                <a id="link_` + post.pid + `" href="#like" onclick="
                                    $.ajax({
                                        type : 'POST',
                                        url : '/api/changeLikeToPost',           
                                        data: { token: $.cookie('token'), pid: ` + post.pid + `},
                                        success:function (jqXHR, textStatus, errorThrown) {
                                            document.getElementById('link_` + post.pid + `').textContent = 'LIKE ' + jqXHR.response;
                                        }
                                    });
                                ">LIKE ` + post.likes_count + `</a>
                            </div>
                            <div class="divComment">
                                <a href="blog_comments.html?post_id=` + post.pid + `&isPost=true">COMMENT</a>
                            </div>
                        </div>
                    </div>`
                }

                document.body.insertAdjacentHTML("beforeend", resp);

            }
        }
    });
}

$("#photo").live('change', function (event) {
    var fileList = event.target.files;
    console.log(fileList);
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
                url: '/api/uploadAvatar',
                type: 'PUT',
                data: '{"token":"' + $.cookie('token') + '", "formData": [' + fileByteArray + ']}',
                cache: false,
                contentType: false,
                processData: false,
                success: function (jqXHR, textStatus, errorThrown) {
                    document.location.reload(true);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert("Ошибка");
                }

            });
        }
    }


});


function logout() {
    $.cookie("token", '', $.extend({}, null, {expires: -1}));
    window.location.href = './index.html';
}


