$.ajax({
    type: 'POST',
    url: '/api/getMemberPosts',
    data: {token: $.cookie('token')},
    success: function (jqXHR, textStatus, errorThrown) {
        var token = jqXHR.response;

        for (var i = jqXHR.length - 1; i >= 0; i--) {
            console.log(jqXHR[i]);

            var post = jqXHR[i];

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
                                ">LIKE ` + post.likes_count + `</a>
                            </div>
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
                                        url : 'http://localhost:8080/api/changeLikeToPost',           
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