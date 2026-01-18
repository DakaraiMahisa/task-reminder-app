document.addEventListener('DOMContentLoaded', function () {
    const taskForm = document.querySelector('.task-form');
    const dueDateInput = document.getElementById('dueDate');

    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast-message ${type}`;

        const icon = type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation';

        toast.innerHTML = `
            <i class="fa-solid ${icon}"></i>
            <span>${message}</span>
        `;

        toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'fadeOut 0.5s ease forwards';
            setTimeout(() => toast.remove(), 500);
        }, 3500);
    }

    taskForm.addEventListener('submit', function (event) {
        const selectedDate = new Date(dueDateInput.value);
        const now = new Date();

        if (dueDateInput.value && selectedDate < now) {
            event.preventDefault();

            showToast("Invalid Date: The deadline cannot be in the past.", "error");

            dueDateInput.style.borderColor = "#e11d48";
            dueDateInput.focus();
        }
    });

    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('success')) {
        showToast("Task updated successfully!", "success");
    }
});