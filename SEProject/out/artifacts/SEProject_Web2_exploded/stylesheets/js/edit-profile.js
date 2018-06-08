$(document).ready(function () {

    var EditProfile = (function ($, window, document) {
        var editProfile = {};

        editProfile.getDetail = function () {
            $('#pleaseWaitDialog').modal();
            $.ajax({
                url: "/get-my-profile",
                type: "post",
                data: {
                    token: window.localStorage.getItem("token"),
                },
                success: function (response) {
                    console.log(response);
                    if (response["verify_token"]) {
                        if (response["success"]) {

                            $("#email").val(response['email']);

                            $("#nick-name").val(response['user_name']);

                            $("#phone").val(response['phone_number']);

                            $("#dob").val(response['DOB']);
                        }
                        else {
                            alert("INTERNAL ERROR");
                        }
                        $('#pleaseWaitDialog').modal('hide');

                    } else {
                        window.location = "/login";
                        $('#pleaseWaitDialog').modal('hide');

                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                }
            });
        };

        editProfile.submitEdit = function () {
            $('#pleaseWaitDialog').modal();

            $.ajax({
                url: "/edit-my-profile",
                type: "post",
                data: {
                    token: window.localStorage.getItem("token"),
                    user_name: $("#nick-name").val(),
                    DOB: $("#dob").val(),
                    phone_number: $("#phone").val()
                },
                success: function (response) {
                    console.log(response);
                    if (response["verify_token"]) {
                        if (response["success"]) {

                            // window.location = "/get-my-profile"
                        }
                        else {
                            alert("INTERNAL ERROR");
                        }
                        $('#pleaseWaitDialog').modal('hide');

                    } else {
                        window.location = "/login";
                        $('#pleaseWaitDialog').modal('hide');

                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                }
            });
        };
        return editProfile;
    }(window.jQuery, window, document));

    $(function () {
        $('.datepicker').datetimepicker({
            format: 'YYYY-MM-DD'
        });
        $('body').onload = EditProfile.getDetail();

        $("body").on("keypress", "#datepicker", function (e) {
            e.preventDefault();
        });

        $("body").on("click", "#edit-profile", function (e) {
            e.preventDefault();
            EditProfile.submitEdit();
        });
    });
});