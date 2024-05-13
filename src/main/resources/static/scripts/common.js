const toggle = document.querySelector(".menu-toggle");
const menu = document.querySelector(".menu-nav-list");

function toggleMenu() {
    if (menu.classList.contains("expanded")) {
        menu.classList.remove("expanded");
        toggle.querySelector('a').innerHTML = '<i id="menu-button-icon" class="fa-regular fa-bars"></i>';
    } else {
        menu.classList.add("expanded");
        toggle.querySelector('a').innerHTML = '<i id="menu-button-icon" class="fa-regular fa-x"></i>';
    }
}

toggle.addEventListener("click", toggleMenu, false);