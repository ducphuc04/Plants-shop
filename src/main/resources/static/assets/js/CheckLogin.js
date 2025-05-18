function renderAuthArea() {
    const authArea = document.getElementById("auth-area");
    const token = localStorage.getItem("access_token");

    if (!authArea) return;

    if (token) {

        authArea.innerHTML = `
        <!-- Nút giỏ hàng -->
        <a href="/shopping-cart.html">
          <button class="btn btn-primary me-3" type="button" style="background: var(--bs-primary);">
            <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor"
              viewBox="0 0 16 16" class="bi bi-cart3" style="width: 20px; height: 20px;">
              <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 
                .49.598l-1 5a.5.5 0 0 1-.465.401l-9.397.472L4.415 11H13a.5.5 0 0 1 
                0 1H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 
                1-.5-.5M3.102 4l.84 4.479 9.144-.459L13.89 4H3.102zM5 
                12a2 2 0 1 0 0 4 2 2 0 0 0 0-4m7 0a2 2 0 1 0 0 4 2 2 0 
                0 0 0-4m-7 1a1 1 0 1 1 0 2 1 1 0 0 1 0-2m7 0a1 1 0 1 1 
                0 2 1 1 0 0 1 0-2"></path>
            </svg>
          </button>
        </a>

        <!-- Dropdown user -->
<!--        <div class="dropdown">-->
<!--          <button class="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown"-->
<!--            aria-expanded="false">-->
<!--            <i class="far fa-user"></i>-->
<!--          </button>-->
<!--          <ul class="dropdown-menu">-->
<!--            <li><a class="dropdown-item" href="/manage-account">Quản lý tài khoản</a></li>-->
<!--            <li><a class="dropdown-item" href="#" onclick="logout()">Đăng xuất</a></li>-->
<!--          </ul>-->
<!--        </div>-->
        <div class="dropdown">
            <button class="btn btn-primary dropdown-toggle pb-2 mt-0 mb-2" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="far fa-user"></i>
            </button>
            <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="manage-account">Quản lý tài khoản</a></li>
                <li><a class="dropdown-item" href="index">Đăng xuất</a></li>
            </ul>
        </div>
      `;
    } else {
        // ❌ Nếu chưa đăng nhập
        authArea.innerHTML = `
        <a href="/login">
          <button class="btn btn-primary">
            <i class="fas fa-sign-in-alt"></i> Đăng nhập
          </button>
        </a>
      `;
    }
}

// Hàm logout
function logout() {
    localStorage.removeItem("access_token");
    window.location.href = "/login";
}

// Gọi khi trang tải
document.addEventListener("DOMContentLoaded", renderAuthArea);