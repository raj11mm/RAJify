(() => {
    const storageKey = "rajify-theme";
    const dark = "dark";
    const light = "light";

    const getPreferredTheme = () => {
        const saved = localStorage.getItem(storageKey);
        if (saved === dark || saved === light) {
            return saved;
        }
        return window.matchMedia("(prefers-color-scheme: dark)").matches ? dark : light;
    };

    const applyTheme = (theme) => {
        document.documentElement.setAttribute("data-theme", theme);
        const toggle = document.getElementById("themeToggle");
        if (toggle) {
            const icon = toggle.querySelector(".theme-icon");
            const label = toggle.querySelector(".theme-label");
            if (icon) {
                icon.textContent = theme === dark ? "sun" : "moon";
            }
            if (label) {
                label.textContent = theme === dark ? "Light" : "Dark";
            }
        }
    };

    document.addEventListener("DOMContentLoaded", () => {
        applyTheme(getPreferredTheme());

        const toggle = document.getElementById("themeToggle");
        if (!toggle) {
            return;
        }
        toggle.addEventListener("click", () => {
            const current = document.documentElement.getAttribute("data-theme") || light;
            const next = current === dark ? light : dark;
            localStorage.setItem(storageKey, next);
            applyTheme(next);
        });
    });
})();
