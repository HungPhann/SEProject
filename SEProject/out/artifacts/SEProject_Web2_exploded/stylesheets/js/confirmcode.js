$.ajax({
    url: "/verify-token",
    async:false,
    type: "post",
    data: {
        token: window.localStorage.getItem("token")
    },
    success: function (response) {
        if (response["verify_token"]) {
            window.location = "/"
        }
    },
    error: function (jqXHR, textStatus, errorThrown) {
        alert("Error");
    }
});

$(document).ready(function () {

    $("body").on("click", "#confirm_button", function () {
        $.ajax({
            url: "/check-confirmation-code",
            type: "post",
            data: {
                email: $("#email").val(),
                confirmation_code: $("#confirmation_code").val()
            },
            success: function (response) {
                if (response["success"]) {
                    window.localStorage.setItem("token", response["token"]);
                    window.location = "/"
                }
                else {
                    $("#error").show();
                    $("#error").html("Wrong confirm code");
                }


            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Error");
            }
        });
    });
});