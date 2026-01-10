document.addEventListener('DOMContentLoaded', () => {

    let time = 300;
    const expiryDisplay = document.getElementById('expiryTimer');

    const countdown = setInterval(() => {
        let minutes = Math.floor(time / 60);
        let seconds = time % 60;

        seconds = seconds < 10 ? '0' + seconds : seconds;
        minutes = minutes < 10 ? '0' + minutes : minutes;

        expiryDisplay.innerHTML = `${minutes}:${seconds}`;

        if (time <= 0) {
            clearInterval(countdown);
            expiryDisplay.innerHTML = "EXPIRED";
            document.querySelector('.verify-btn').disabled = true;
        }
        time--;
    }, 1000);

    // 2. Auto-focus and Hidden Field logic
    const inputs = document.querySelectorAll('.otp-inputs input');
    const hiddenInput = document.getElementById('otpHidden');

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
});