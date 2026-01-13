document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.nav-toggle');
    const navRight = document.querySelector('.nav-right');

    if (toggle) {
        toggle.addEventListener('click', () => {
            navRight.classList.toggle('active');
        });
    }
});