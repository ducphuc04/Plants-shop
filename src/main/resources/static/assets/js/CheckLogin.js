function updateNavigation(){
    const loginNavItem = document.getElementById('login-nav-item');

    const userDropdown = document.getElementById('user-dropdown');

    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    if (token){
        if (loginNavItem)   loginNavItem.classList.add('d-none');
        if (userDropdown)   userDropdown.classList.remove('d-none');
    }else{
        if (loginNavItem)   loginNavItem.classList.remove('d-none');
        if (userDropdown)   userDropdown.classList.add('d-none');
    }
}

// Hàm logout
function logout() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("access_token");
    window.location.href = "/login";
}

// Gọi khi trang tải
document.addEventListener("DOMContentLoaded", function() {
    updateNavigation();
    const logoutButton = document.querySelector('#user-dropdown .dropdown-item[onclick="logout()"]');
    if (logoutButton) {
        logoutButton.addEventListener('click', function () {
            event.preventDefault();
            logout();
        });
    }
});