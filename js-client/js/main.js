var USER_CHANGE = "http://localhost:8088/user";
var USERS_URL = "http://localhost:8088/users";

$.dto = null;

// Register listeners
$('#btnSave').click(function () {
    if ($('#userId').val() == '')
        addUser();
    else
        updateUser();
    return false;
});

$('#btnClean').click(function () {
    $("#userId").val("");
    $("#login").val("");
    $("#password").val("");
    $("#description").val("");
});

$(document).on("click", "a", function() {
    var action = $(this).text();
    var selectedUserId = $(this).data("id");
    if (action != "delete") {
        $.each(dto, function (index) {
            if (dto[index].userId == selectedUserId) {
                $("#userId").val(selectedUserId);
                $("#login").val(dto[index].login);
                $("#password").val(dto[index].password);
                $("#description").val(dto[index].description);
            }
        });
    } else {
        deleteUser(selectedUserId);
    }
});

findAll();

function findAll() {
    console.log('findAll');
    $.ajax({
        type: 'GET',
        url: USERS_URL,
        dataType: "json", // data type of response
        success: renderList,
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
            alert('findAll: ' + textStatus);
        }
    });
}

function addUser() {
    console.log('addUser');
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: USER_CHANGE,
        dataType: "json",
        data: formToJSON(),
        success: function (data, textStatus, jqXHR) {
            alert('User created successfully');
            console.log('User created successfully');
            findAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('addUser error: ' + errorThrown);
        }
    });
}

function updateUser() {
    console.log('updateUser');
    $.ajax({
        type: 'PUT',
        contentType: 'application/json',
        url: USER_CHANGE,
        data: formToJSON(),
        success: function (data, textStatus, jqXHR) {
            alert('User updated successfully');
            console.log('User updated successfully');
            findAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('updateUser error: ' + errorThrown);
        }
    });
}

function deleteUser(userId) {
    console.log('deleteUser');
    $.ajax({
        type: 'DELETE',
        contentType: 'application/json',
        url: USER_CHANGE + '/' + userId,
        success: function (data, textStatus, jqXHR) {
            alert('User delete successfully');
            findAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('deleteUser error: ' + errorThrown);
        }
    });
}


function renderList(data) {
    dto = data == null ? [] : (data instanceof Array ? data : [data]);
    $('#userList tr').remove();
    $.each(dto, function (index, user) {
        drawRow(user);
    });
}

function drawRow(user) {
    var row = $("<tr />")
    $("#userList").append(row);
    row.append($("<td>" + '<a href="#" data-id="' + user.userId + '">' + user.login + '</a></td>'));
    row.append($("<td>" + user.description + "</td>"));
    row.append($("<td>" + '<a href="#" data-id="' + user.userId + '">delete</a></td>'));
}

function formToJSON() {
    var userId = $('#userId').val();
    return JSON.stringify({
        "userId": userId == "" ? null : userId,
        "login": $('#login').val(),
        "password": $('#password').val(),
        "description" : $('#description').val()
    });
}