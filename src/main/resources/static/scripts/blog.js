$.ajax({
    type: 'POST',
    url: '/api/getAllBlogPosts',
    data: {token: $.cookie('token')},
    success: function (jqXHR, textStatus, errorThrown) {
        var token = jqXHR.response;

        for (var i = jqXHR.length - 1; i >= 0; i--) {
            console.log(jqXHR[i]);

            var post = jqXHR[i];

            var resp = ``;

            resp = `<div class="divPost">
                        <table>
                            <tr>
                                <th><h2 class="fontFIO"><center>` + post.title + `</center></h2><!-- Название соревнований--></th>
                            </tr>
                            <tr>
                                <th><center>` + post.body + `</center></th>
                            </tr>
                            <tr>
                                <th><center><img src="` + post.image + `"></center></th>
                            </tr>
                        </table>
                        <br>
                        <div>
                            <div class="divLike">
                                <a id="link_` + post.pid + `" href="#like" onclick="
                                    $.ajax({
                                        type : 'POST',
                                        url : '/api/changeLikeToBlogPost',           
                                        data: { token: $.cookie('token'), pid: ` + post.pid + `},
                                        success:function (jqXHR, textStatus, errorThrown) {
                                            document.getElementById('link_` + post.pid + `').textContent = 'LIKE ' + jqXHR.response;

                                        }
                                    });
                                ">LIKE ` + post.likes_count + `</a>
                            </div>
                            <div class="divComment">
                                <a href="blog_comments.html?post_id=` + post.pid +`">COMMENT</a>
                            </div>
                        </div>
                    </div>`


            document.body.insertAdjacentHTML("beforeend", resp);

        }
    }
});  
