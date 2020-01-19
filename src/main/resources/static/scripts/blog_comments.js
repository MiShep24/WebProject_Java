var url_string = window.location.href; //window.location.href
var url = new URL(url_string);
var pid = url.searchParams.get("post_id");
var isPost = url.searchParams.get("isPost");


$.ajax({
    type: 'POST',
    url: isPost ? '/api/getPostComments' : '/api/getBlogPostComments',
    data: {token: $.cookie('token'), pid: pid},
    success: function (jqXHR, textStatus, errorThrown) {
        console.log(jqXHR);
        const comments_container = document.getElementById("comments_container")
        let html = "";
        document.getElementById("title").innerHTML = "Чат \"" + jqXHR.title + "\"";
        for (let i = 0; i < jqXHR.comments.length; i++) {
            const comment = jqXHR.comments[i];

            if (comment.isMy) {
                if (comment.isOrganizer) {
                    html = html + ` <div class="messageMe">
            <div><img src="` + comment.owner.avatar + `"/></div>
            <div class="textMessageMeOrg">
                ` + comment.text + `
                <div class="signature">sent by me at ` + comment.date + `</div>
            </div>
        </div>`;
                } else {
                    html = html + ` <div class="messageMe">
            <div><img src="` + comment.owner.avatar + `"/></div>
            <div class="textMessageMe">
                ` + comment.text + `
                <div class="signature">sent by me at ` + comment.date + `</div>
            </div>
        </div>`;
                }
            } else {
                if (comment.isOrganizer) {
                    html = html + ` <div class="messageMembers">
            <div><a href="#"><img src="` + comment.owner.avatar + `"/></a></div>
            <div class="textMessageOrg">
                ` + comment.text + `
                <div class="signature">sent by <a href="/profile.html?user_id=` + comment.owner.id + `">` + comment.owner.firstName + ' ' + comment.owner.lastName + `</a> at ` + comment.date + `</div>
            </div>
        </div>`;
                } else {
                    html = html + `  <div class="messageMembers">
            <div><a href="#"><img src="` + comment.owner.avatar + `"/></a></div>
            <div class="textMessageMembers">
                ` + comment.text + `
                <div class="signature">sent by <a href="/profile.html?user_id=` + comment.owner.id + `">` + comment.owner.firstName + ' ' + comment.owner.lastName + `</a> at ` + comment.date + `</div>
            </div>
        </div>`;
                }
            }
        }
        comments_container.innerHTML = html;

    }
});

function send() {
    const inputtext = $('#inputtext').val();
    if (inputtext.trim() != "") {
        $.ajax({
            type: 'POST',
            url: isPost ? '/api/addPostComments' : '/api/addBlogPostComments',
            data: {token: $.cookie('token'), pid: pid, text: inputtext},
            success: function (jqXHR, textStatus, errorThrown) {
                window.location.reload();
            }
        });
    }

}