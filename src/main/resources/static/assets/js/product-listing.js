// Biến lưu trữ trạng thái hiện tại
let currentPage = 0;
let currentCategory = null;
let totalPages = 0;
let pageSize = 8;

// Hàm tải danh sách danh mục
function loadCategories() {
    fetch('/product/category')
        .then(response => response.json())
        .then(data => {
            if (data && data.result) {
                renderCategories(data.result);
            }
        })
        .catch(error => {
            console.error('Error loading categories:', error);
        });
}

// Hàm tải danh sách sản phẩm
function loadProducts(page = 0, category = null) {
    // Lưu trạng thái hiện tại
    currentPage = page;
    if (category !== undefined) {
        currentCategory = category;
    }
    
    // Xây dựng URL với các tham số
    let url = `/product/listProduct?page=${page}&size=${pageSize}`;
    if (currentCategory) {
        url += `&category=${encodeURIComponent(currentCategory)}`;
    }
    
    // Gọi API
    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data && data.result) {
                renderProducts(data.result.items || []);
                renderPagination(data.result.currentPage, data.result.totalPages);
                totalPages = data.result.totalPages;
            }
        })
        .catch(error => {
            console.error('Error loading products:', error);
        });
}

// Hàm hiển thị danh mục
function renderCategories(categories) {
    const categoryListElement = document.getElementById('category-list');
    if (!categoryListElement) return;
    
    let html = `<ul class="nav nav-pills">
                    <li class="nav-item">
                        <a class="nav-link ${!currentCategory ? 'active' : ''}" 
                           href="#" onclick="loadProducts(0, null); return false;">
                           Tất cả
                        </a>
                    </li>`;
    
    categories.forEach(category => {
        html += `
            <li class="nav-item">
                <a class="nav-link ${currentCategory === category ? 'active' : ''}" 
                   href="#" onclick="loadProducts(0, '${category}'); return false;">
                   ${category}
                </a>
            </li>`;
    });
    
    html += '</ul>';
    categoryListElement.innerHTML = html;
}

// Hàm hiển thị sản phẩm
function renderProducts(products) {
    const productListElement = document.getElementById('product-list');
    if (!productListElement) return;
    
    if (!products || products.length === 0) {
        productListElement.innerHTML = '<div class="col-12 text-center my-5"><h4>Không tìm thấy sản phẩm nào</h4></div>';
        return;
    }
    
    let html = '';
    products.forEach(product => {
        html += `
            <div class="col-md-3 mb-4">
                <a href="/product-detail-page?product=${product.productId}" class="text-decoration-none text-dark">
                    <div class="card h-100 border-3">
                        <div class="card-body text-center">
                            <img class="img-fluid" src="${product.image}" alt="${product.productName}">
                            <p class="card-title" style="text-align: left; font-size: 22px; font-weight: bold; height: 66px;">
                                ${product.productName}
                            </p>
                            <p class="card-description-1" style="text-align: left; font-size: 20px; width: 160px; height: 30px;">
                                ${formatPrice(product.price)} VNĐ
                            </p>
                            <button class="btn btn-primary" type="button" 
                                    style="color: #198754; border-color: #198754; border-radius: 20px; font-size: 20px; background: #ffffff;">
                                Mua ngay
                            </button>
                        </div>
                    </div>
                </a>
            </div>`;
    });
    
    productListElement.innerHTML = html;
}

// Hàm hiển thị phân trang
function renderPagination(currentPage, totalPages) {
    const paginationElement = document.getElementById('pagination');
    if (!paginationElement) return;
    
    let html = '';
    
    // Nút Previous
    html += `
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadProducts(${currentPage - 1}); return false;">
                &laquo;
            </a>
        </li>`;
    
    // Các trang
    for (let i = 0; i < totalPages; i++) {
        if (i === currentPage) {
            html += `<li class="page-item active"><span class="page-link">${i + 1}</span></li>`;
        } else {
            html += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="loadProducts(${i}); return false;">
                        ${i + 1}
                    </a>
                </li>`;
        }
    }
    
    // Nút Next
    html += `
        <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadProducts(${currentPage + 1}); return false;">
                &raquo;
            </a>
        </li>`;
    
    paginationElement.innerHTML = html;
}

// Hàm định dạng giá tiền
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN').format(price);
}

// Chạy khi trang được tải
document.addEventListener('DOMContentLoaded', function() {
    loadCategories();
    loadProducts();
});