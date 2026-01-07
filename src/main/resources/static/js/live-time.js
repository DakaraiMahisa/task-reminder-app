document.addEventListener("DOMContentLoaded", () => {
    const el = document.getElementById("liveTime");
    if (!el) return;

    function updateTime() {
        const now = new Date();
        el.textContent = now.toLocaleTimeString();
    }

    updateTime();
    setInterval(updateTime, 1000);
});
