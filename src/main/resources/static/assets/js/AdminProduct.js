// Constants
const INITIAL_PRODUCTS_PER_CATEGORY = 4;
const LOAD_MORE_PRODUCTS_COUNT = 8;
let categories = [];
let categoryProducts = {};

// Initialize the page
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    if (!checkAdminAccess()) {
        return;
    }
    
    // Create the container for categories
    const mainContent = document.querySelector('.main-content section');
    const categoriesContainer = document.createElement('div');
    categoriesContainer.id = 'categories-container';
    mainContent.appendChild(categoriesContainer);
    
    // Fetch all categories
    fetchCategories();
});

// Check if user has admin access
function checkAdminAccess() {
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');
    
    if (!token) {
        window.location.href = "/login?returnUrl=/admin-product";
        return false;
    }
    
    // Parse the JWT to check if user has admin role
    try {
        const decodedToken = parseJwt(token);
        if (decodedToken.scope !== 'ADMIN' && decodedToken.scope !== 'EMPLOYEE') {
            window.location.href = "/index";
            return false;
        }
    } catch (error) {
        console.error('Error parsing token:', error);
        window.location.href = "/login";
        return false;
    }
    
    return true;
}

// Parse JWT token
function parseJwt(token) {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
        return null;
    }
}

// Fetch all categories
function fetchCategories() {
    fetch('/product/category', {
        headers: {
            'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token'))
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch categories');
        }
        return response.json();
    })
    .then(data => {
        console.log(data);
        if (data && data.result) {
            categories = data.result;
            const categoriesContainer = document.getElementById('categories-container');
            categoriesContainer.innerHTML = ''; // Clear container
            
            // For each category, create a section
            categories.forEach((category, index) => {
                createCategorySection(categoriesContainer, category, index);
                // Load initial products for this category
                loadCategoryProducts(category, 0, INITIAL_PRODUCTS_PER_CATEGORY, index);
            });
        }
    })
    .catch(error => {
        console.error('Error fetching categories:', error);
        showErrorMessage('Failed to load categories');
    });
}

// Create a section for each category
function createCategorySection(container, category, index) {
    const categorySection = document.createElement('div');
    categorySection.className = 'container mt-0 pt-5';
    categorySection.innerHTML = `
        <div class="mt-4 mb-4">
            <span style="font-size: 25px;" class="me-4"><strong>${category}</strong></span>
            <a href="admin-add-product?category=${encodeURIComponent(category)}"><i class="far fa-plus-square" style="font-size: 23px;"></i></a>
        </div>
        <div class="row" id="product-row-${index}">
            <!-- Products will be loaded here -->
        </div>
        <div class="collapse" id="collapseCategory${index}">
            <div class="row mt-4" id="load-more-products-${index}">
                <!-- Additional products will be loaded here -->
            </div>
        </div>
        <div class="text-center mt-5">
            <button class="btn btn-primary" type="button" style="color: #000000;background: rgb(163,177,173);font-size: 26px;" 
                    data-bs-toggle="collapse" data-bs-target="#collapseCategory${index}" 
                    onclick="loadMoreProducts('${category}', ${index})">Xem thêm</button>
        </div>
    `;
    container.appendChild(categorySection);
}

// Load products for a specific category
function loadCategoryProducts(category, page, size, categoryIndex) {
    const url = `/product/listProduct?category=${encodeURIComponent(category)}&page=${page}&size=${size}`;
    
    fetch(url, {
        headers: {
            'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token'))
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Failed to load products for category ${category}`);
        }
        return response.json();
    })
    .then(data => {
        if (data && data.result && data.result.items) {
            const products = data.result.items;
            // Store products for this category
            if (!categoryProducts[category]) {
                categoryProducts[category] = [];
            }
            
            // Only add products that aren't already in the list
            products.forEach(product => {
                if (!categoryProducts[category].some(p => p.productId === product.productId)) {
                    categoryProducts[category].push(product);
                }
            });
            
            // Render initial products (up to 4)
            renderCategoryProducts(category, categoryIndex, false);
        }
    })
    .catch(error => {
        console.error(`Error loading products for category ${category}:`, error);
        showErrorMessage(`Failed to load products for ${category}`);
    });
}

// Render products for a category
function renderCategoryProducts(category, categoryIndex, isLoadMore) {
    const products = categoryProducts[category] || [];
    const container = document.getElementById(isLoadMore ? 
        `load-more-products-${categoryIndex}` : `product-row-${categoryIndex}`);
    
    if (!container) return;
    
    // Clear container if it's not load more
    if (!isLoadMore) {
        container.innerHTML = '';
    }
    
    // Determine which products to show
    let productsToShow = [];
    if (isLoadMore) {
        // For load more, show products 5 through 12 (if available)
        productsToShow = products.slice(INITIAL_PRODUCTS_PER_CATEGORY, 
            INITIAL_PRODUCTS_PER_CATEGORY + LOAD_MORE_PRODUCTS_COUNT);
    } else {
        // For initial load, show first 4 products
        productsToShow = products.slice(0, INITIAL_PRODUCTS_PER_CATEGORY);
    }
    
    // Create product cards
    productsToShow.forEach(product => {
        const productCol = document.createElement('div');
        productCol.className = 'col-md-3 mb-4';
        productCol.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <div><img class="img-fluid" src="${product.image}" width="534" height="560"></div>
                    <div class="mb-2 mt-2 pb-0" style="font-size: 21px;height: 63px;">
                        <span style="height: 63px;">${product.productName}</span>
                    </div>
                    <div class="d-flex justify-content-between" style="height: 60px;">
                        <span class="d-xl-flex align-items-xl-center" style="font-size: 20px;font-weight: bold;">${product.price}</span>
                        <span class="d-xl-flex align-items-xl-center">Stock: ${product.stock}</span>
                    </div>
                    <div class="text-center">
                        <a href="admin-edit?productId=${product.productId}">
                            <span class="me-3" style="font-size: 20px;">Edit</span>
                        </a>
                        <a href="#" onclick="deleteProduct(${product.productId})" class="delete-product" data-product-id="${product.productId}">
                            <span class="ms-3" style="color: rgb(220,15,15);font-size: 20px;text-decoration: underline;">Remove</span>
                        </a>
                    </div>
                </div>
            </div>
        `;
        container.appendChild(productCol);
    });
    
    // If no products were found, show a message
    if (productsToShow.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center mt-3 mb-3">
                <p>No products found for this category.</p>
            </div>
        `;
    }
}

// Load more products when clicking "Xem thêm" button
function loadMoreProducts(category, categoryIndex) {
    // Check if we need to fetch more products or if we already have enough
    const products = categoryProducts[category] || [];
    if (products.length <= INITIAL_PRODUCTS_PER_CATEGORY) {
        // We need to fetch more products
        loadCategoryProducts(category, 1, LOAD_MORE_PRODUCTS_COUNT, categoryIndex);
    }
    
    // Render the additional products
    renderCategoryProducts(category, categoryIndex, true);
}

// Delete a product
function deleteProduct(productId) {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.isConfirmed) {
            // Send request to delete the product
            fetch(`/product/deleteProduct/${productId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token'))
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to delete product');
                }
                return response.json();
            })
            .then(data => {
                if (data && data.result) {
                    Swal.fire(
                        'Deleted!',
                        'The product has been deleted.',
                        'success'
                    ).then(() => {
                        // Refresh the page to reflect changes
                        location.reload();
                    });
                } else {
                    Swal.fire(
                        'Error!',
                        data.message || 'Failed to delete the product.',
                        'error'
                    );
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showErrorMessage('An unexpected error occurred');
            });
        }
    });
}

// Helper function to show error messages
function showErrorMessage(message) {
    Swal.fire({
        icon: 'error',
        title: 'Oops...',
        text: message
    });
}