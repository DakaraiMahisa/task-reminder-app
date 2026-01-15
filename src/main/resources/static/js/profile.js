document.addEventListener('DOMContentLoaded', function () {

    const profileInput = document.getElementById('profileImage');
    const profilePreview = document.getElementById('profilePreview');

    if (profileInput && profilePreview) {
        profileInput.onchange = function () {
            const [file] = this.files;
            if (file) {
                profilePreview.src = URL.createObjectURL(file);
            }
        };
    }

    const urlParams = new URLSearchParams(window.location.search);

    function triggerToast(id) {
        const toast = document.getElementById(id);
        if (toast) {
            toast.style.display = 'flex';
            toast.style.alignItems = 'center';
            toast.style.gap = '10px';
            setTimeout(() => { toast.style.display = 'none'; }, 4000);
        }
    }

    if (urlParams.has('success')) triggerToast('successToast');
    if (urlParams.has('error')) triggerToast('errorToast');
});