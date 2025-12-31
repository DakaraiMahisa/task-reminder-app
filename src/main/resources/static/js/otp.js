<script>
    const inputs = document.querySelectorAll('.otp-inputs input');
    const hidden = document.getElementById('otpHidden');

    inputs.forEach((input, index) => {
        input.addEventListener('input', () => {
            input.value = input.value.replace(/[^0-9]/g, '');
            if (input.value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
            hidden.value = [...inputs].map(i => i.value).join('');
        });
    });

    const resendBtn = document.getElementById('resendBtn');
    const resendText = document.getElementById('resendText');
    const timerText = document.getElementById('timerText');
    const timerSpan = document.getElementById('timer');
    const email = document.querySelector('input[name="email"]').value;

    let countdown = 30;

    resendBtn.addEventListener('click', async () => {
        resendBtn.disabled = true;
        resendText.style.display = 'none';
        timerText.style.display = 'block';

        await fetch('/auth/resend-otp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `email=${encodeURIComponent(email)}`
        });

        const interval = setInterval(() => {
            countdown--;
            timerSpan.textContent = countdown;

            if (countdown === 0) {
                clearInterval(interval);
                resendBtn.disabled = false;
                resendText.style.display = 'block';
                timerText.style.display = 'none';
                countdown = 30;
            }
        }, 1000);
    });
</script>
