document.addEventListener("DOMContentLoaded", function () {

    const notifications = document.querySelectorAll(
        ".success-message, .error-message"
    );

    if (notifications.length === 0) {
        return;
    }

    notifications.forEach(message => {
        setTimeout(() => {
            message.style.opacity = "0";
            message.style.transition = "opacity 0.5s ease";

            setTimeout(() => {
                message.remove();
            }, 500);
        }, 4000);
    });
});
