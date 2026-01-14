document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('resetPasswordForm');
    const passwordInput = document.getElementById('password');
    const confirmInput = document.getElementById('confirmPassword');
    const passwordError = document.getElementById('passwordError');
    const confirmError = document.getElementById('confirmError');

    form.addEventListener('submit', function(event) {
        // Reset previous error messages
        passwordError.style.display = 'none';
        confirmError.style.display = 'none';

        const password = passwordInput.value.trim();
        const confirm = confirmInput.value.trim();
        let valid = true;

        // Password complexity check
        const complexityRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
        if (!complexityRegex.test(password)) {
            passwordError.textContent = 'Password must be at least 8 characters, include uppercase, lowercase, and a number.';
            passwordError.style.display = 'block';
            valid = false;
        }

        // Password match check
        if (password !== confirm) {
            confirmError.textContent = 'Passwords do not match.';
            confirmError.style.display = 'block';
            valid = false;
        }

        if (!valid) {
            event.preventDefault(); // Stop form submission
        }
    });
});
