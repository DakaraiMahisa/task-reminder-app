(function () {
    const savedTheme = localStorage.getItem("theme");

    if (savedTheme) {
        document.body.classList.toggle("dark", savedTheme === "dark");
    } else {
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        document.body.classList.toggle("dark", prefersDark);
    }
})();

function toggleTheme() {
    const isDark = document.body.classList.toggle("dark");
    localStorage.setItem("theme", isDark ? "dark" : "light");

    const icon = document.querySelector(".theme-toggle i");
    icon.className = isDark
        ? "fa-solid fa-sun"
        : "fa-solid fa-moon";
}

