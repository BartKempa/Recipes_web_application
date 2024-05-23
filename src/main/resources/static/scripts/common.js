const toggle = document.querySelector(".menu-toggle-container");
const menu = document.querySelector(".menu-nav-list");
const toggleUser = document.querySelector(".user-toggle-container");
const menuUser = document.querySelector(".menu-top");

function toggleMenu() {
    if (menu.classList.contains("expanded")) {
        menu.classList.remove("expanded");
        toggle.querySelector('a').innerHTML = '<i id="toggle-icon" class="fa-solid fa-bars"></i>';
    } else {
        menu.classList.add("expanded");
        toggle.querySelector('a').innerHTML = '<i id="toggle-icon" class="fa-regular fa-x"></i>';
    }
}

toggle.addEventListener("click", toggleMenu, false);

function toggleMenuUser() {
    if (menuUser.classList.contains("expanded")) {
        menuUser.classList.remove("expanded");
        toggleUser.querySelector('a').innerHTML = '<i id="toggle-user-icon" class="fa-regular fa-user fa-lg"></i>';
    } else {
        menuUser.classList.add("expanded");
        toggleUser.querySelector('a').innerHTML = '<i id="toggle-user-icon" class="fa-solid fa-user-minus"></i>';
    }
}

toggleUser.addEventListener("click", toggleMenuUser, false);