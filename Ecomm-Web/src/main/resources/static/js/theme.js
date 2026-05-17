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
        if (toggle) {
            toggle.addEventListener("click", () => {
                const current = document.documentElement.getAttribute("data-theme") || light;
                const next = current === dark ? light : dark;
                localStorage.setItem(storageKey, next);
                applyTheme(next);
            });
        }

        const grid = document.getElementById("productGrid");
        const sort = document.getElementById("sortProducts");
        if (grid && sort) {
            const originalCards = Array.from(grid.querySelectorAll(".product-card"));
            sort.addEventListener("change", () => {
                const cards = Array.from(grid.querySelectorAll(".product-card"));
                const sorted = sort.value === "featured"
                    ? originalCards
                    : cards.sort((a, b) => {
                        const priceA = Number(a.dataset.price || 0);
                        const priceB = Number(b.dataset.price || 0);
                        const ratingA = Number(a.dataset.rating || 0);
                        const ratingB = Number(b.dataset.rating || 0);

                        if (sort.value === "price-low") {
                            return priceA - priceB;
                        }
                        if (sort.value === "price-high") {
                            return priceB - priceA;
                        }
                        return ratingB - ratingA;
                    });

                sorted.forEach((card) => grid.appendChild(card));
            });
        }

        document.querySelectorAll("[data-view]").forEach((button) => {
            button.addEventListener("click", () => {
                const grid = document.getElementById("productGrid");
                if (!grid) {
                    return;
                }
                const nextView = button.dataset.view;
                grid.classList.toggle("compact-view", nextView === "compact");
                document.querySelectorAll("[data-view]").forEach((viewButton) => {
                    const active = viewButton === button;
                    viewButton.classList.toggle("is-active", active);
                    viewButton.setAttribute("aria-pressed", String(active));
                });
            });
        });

        document.querySelectorAll(".add-cart-btn").forEach((button) => {
            button.addEventListener("click", () => {
                button.classList.add("is-adding");
                button.textContent = "Adding...";
            });
        });
    });
})();
