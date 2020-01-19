var url_string = window.location.href; //window.location.href
var url = new URL(url_string);
var pid = url.searchParams.get("pid");

$.ajax({
    type: 'GET',
    url: '/api/getPostMembers',
    data: {pid: pid},
    success: function (jqXHR, textStatus, errorThrown) {
        for (var i = jqXHR.length - 1; i >= 0; i--) {
            var user = jqXHR[i];
            if (user.userInfo === undefined) {
                document.getElementById("table").insertAdjacentHTML("beforeend", `<tr>
        				<th>` + (i + 1) + `</th>
        				<th>` + user.lastName + `</th>
        				<th>` + user.firstName + `</th>
        				<th>` + user.middleName + `</th>
        				<th>` + user.birthday + `</th>
        				<th></th>
        				<th></th>
        				<th></th>
        				<th></th>
    				</tr>`);
            } else {
                document.getElementById("table").insertAdjacentHTML("beforeend", `<tr>
        				<th>` + (i + 1) + `</th>
        				<th>` + user.lastName + `</th>
        				<th>` + user.firstName + `</th>
        				<th>` + user.middleName + `</th>
        				<th>` + user.birthday + `</th>
        				<th>` + user.userInfo.city + `</th>
        				<th>` + user.userInfo.school + `</th>
        				<th>` + user.userInfo.rang + `</th>
        				<th>` + user.userInfo.trener + `</th>
    				</tr>`);
            }

        }
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert(JSON.parse(jqXHR.responseText).error);
    }
});
