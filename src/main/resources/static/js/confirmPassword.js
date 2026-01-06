document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("registerForm");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const passwordError = document.getElementById("passwordError");

    if (!form) {
        return;
    }

    form.addEventListener("submit", function (event) {
        if (password.value !== confirmPassword.value) {
            event.preventDefault();
            passwordError.style.display = "block";
        } else {
            passwordError.style.display = "none";
        }
    });
});
