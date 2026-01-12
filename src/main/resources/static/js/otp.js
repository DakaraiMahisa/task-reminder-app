document.addEventListener('DOMContentLoaded', () => {
    let time = 300;
    let resendCooldown = 0;
    const expiryDisplay = document.getElementById('expiryTimer');
    const resendBtn = document.getElementById('resendBtn');
    const verifyBtn = document.querySelector('.verify-btn');
    const otpForm = document.getElementById('otpForm');
    const inputs = document.querySelectorAll('.otp-inputs input');
    const hiddenInput = document.getElementById('otpHidden');

    // 1. Global Expiry Timer
    const countdown = setInterval(() => {
        let minutes = Math.floor(time / 60);
        let seconds = time % 60;
        expiryDisplay.innerHTML = `${minutes < 10 ? '0' : ''}${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

        if (time <= 0) {
            clearInterval(countdown);
            expiryDisplay.innerHTML = "EXPIRED";
            verifyBtn.disabled = true;
        }
        time--;
    }, 1000);

    // 2. Resend Cooldown Timer Logic
    function startResendCooldown() {
        resendCooldown = 60;
        resendBtn.disabled = true;
        const cooldownInterval = setInterval(() => {
            resendBtn.innerText = `Resend in ${resendCooldown}s`;
            resendCooldown--;
            if (resendCooldown < 0) {
                clearInterval(cooldownInterval);
                resendBtn.disabled = false;
                resendBtn.innerText = "Resend OTP";
            }
        }, 1000);
    }

    // 3. OTP Input & Hidden Field Logic
    inputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            if (e.target.value.length === 1 && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
            updateHiddenValue();
        });

        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });

    function updateHiddenValue() {
        let otp = "";
        inputs.forEach(input => otp += input.value);
        hiddenInput.value = otp;
    }

    // 4. Handle Verify Button Loading
    otpForm.addEventListener('submit', () => {
        verifyBtn.innerHTML = '<span class="spinner"></span> Verifying...';
        verifyBtn.classList.add('loading');
    });

    // 5. Handle Resend Button with Loading & Cooldown
    resendBtn.addEventListener('click', async () => {
        const email = document.querySelector('input[name="email"]').value;
        const originalText = resendBtn.innerText;

        // UI Loading State
        resendBtn.innerHTML = '<span class="spinner"></span> Sending...';
        resendBtn.disabled = true;

        try {
            const response = await fetch('/auth/resend-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ 'email': email })
            });

            if (response.ok) {
                alert("New OTP sent!");
                time = 300;
                startResendCooldown();
            } else {
                alert("Error. Try again.");
                resendBtn.disabled = false;
                resendBtn.innerText = originalText;
            }
        } catch (error) {
            resendBtn.disabled = false;
            resendBtn.innerText = originalText;
        }
    });
});