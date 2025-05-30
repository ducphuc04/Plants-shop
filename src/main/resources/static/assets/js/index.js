// Document ready function
document.addEventListener('DOMContentLoaded', function() {
    // Fetch categories
    // fetchCategories();
    
    // Fetch featured products
    fetchFeaturedProducts();
});

/**
 * Fetch all categories from the API and display them
 */
// function fetchCategories() {
//     fetch('/product/category')
//         .then(response => {
//             if (!response.ok) {
//                 throw new Error('Không thể tải danh mục sản phẩm');
//             }
//             return response.json();
//         })
//         .then(data => {
//             if (data && data.result) {
//                 const categories = data.result;
//                 displayCategories(categories);
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             Swal.fire({
//                 icon: 'error',
//                 title: 'Lỗi',
//                 text: 'Không thể tải danh mục sản phẩm'
//             });
//         });
// }

/**
 * Define category images - map each category to an image
 */
const categoryImages = {
    'Cây cảnh': 'assets/img/tech/cay-canh.jpg',
    'Chậu cây': 'assets/img/tech/chau-cay.jpg',
    'Phân bón': 'assets/img/tech/phan-bon.jpg',
    'Dụng cụ làm vườn': 'assets/img/tech/dung-cu.jpg'
};

// Default image if category is not in the map
const defaultCategoryImage = 'assets/img/tech/default-category.jpg';

/**
 * Display categories in the categories container
 * @param {Array} categories - List of categories
 */
function displayCategories(categories) {
    const categoriesContainer = document.getElementById('categories-container');
    
    // Limit to 4 categories
    const categoriesToShow = categories.slice(0, 4);
    
    // Clear the container
    categoriesContainer.innerHTML = '';
    
    // Add each category
    // categoriesToShow.forEach(category => {
    //     // Get image for this category or use default
    //     const imageUrl = categoryImages[category] || defaultCategoryImage;
    //
    //     const categoryCard = document.createElement('div');
    //     categoryCard.className = 'col-md-3';
    //     categoryCard.innerHTML = `
    //         <div class="card h-100 border-3">
    //             <div class="card-body text-center">
    //                 <img class="img-fluid" src="${imageUrl}" alt="${category}">
    //                 <h3 class="mt-3 my-3 card-title" style="font-size: 25px;height: 75px;color: #000000;font-weight: bold;">${category}</h3>
    //                 <p class="text-decoration-none my-4 card-description" style="text-align: left;font-size: 20px;height: 90px;color: #000000;">Các loại ${category.toLowerCase()} chất lượng cao</p>
    //                 <a href="features?category=${encodeURIComponent(category)}">
    //                     <button class="btn text-decoration-none mt-2 my-3 card-button" type="button" style="color: #198754;border-color: #198754;border-radius: 20px;font-size: 20px;">Xem ngay</button>
    //                 </a>
    //             </div>
    //         </div>
    //     `;
    //     categoriesContainer.appendChild(categoryCard);
    // });
    
    // If no categories are found
    if (categoriesToShow.length === 0) {
        categoriesContainer.innerHTML = `
            <div class="col-12 text-center">
                <p>Không tìm thấy danh mục sản phẩm</p>
            </div>
        `;
    }
}

/**
 * Fetch featured products from the API
 */
function fetchFeaturedProducts() {
    // We'll fetch all products and then display 8 random ones
    fetch('/product/listProduct?page=0&size=50')
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể tải sản phẩm');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.result && data.result.items) {
                const products = data.result.items;
                
                // Shuffle the products array
                const shuffledProducts = shuffleArray(products);
                
                // Take the first 8 products
                const featuredProducts = shuffledProducts.slice(0, 8);
                
                // Display the products
                displayFeaturedProducts(featuredProducts);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: 'Không thể tải sản phẩm nổi bật'
            });
        });
}

/**
 * Shuffle an array randomly (Fisher-Yates algorithm)
 * @param {Array} array - The array to shuffle
 * @returns {Array} - The shuffled array
 */
function shuffleArray(array) {
    const newArray = [...array];
    for (let i = newArray.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [newArray[i], newArray[j]] = [newArray[j], newArray[i]];
    }
    return newArray;
}

/**
 * Display featured products in the featured products container
 * @param {Array} products - List of products to display
 */
function displayFeaturedProducts(products) {
    const featuredProductsContainer = document.getElementById('featured-products');
    
    // Clear the container
    featuredProductsContainer.innerHTML = '';
    
    // Create product rows
    const firstRow = document.createElement('div');
    firstRow.className = 'row g-3 mb-4';
    
    const secondRow = document.createElement('div');
    secondRow.className = 'row g-3';
    
    // Add products to rows
    products.forEach((product, index) => {
        const productCard = createProductCard(product);
        
        // First 4 products go to first row, next 4 to second row
        if (index < 4) {
            firstRow.appendChild(productCard);
        } else {
            secondRow.appendChild(productCard);
        }
    });
    
    // Add rows to container
    featuredProductsContainer.appendChild(firstRow);
    featuredProductsContainer.appendChild(secondRow);
    
    // If no products are found
    if (products.length === 0) {
        featuredProductsContainer.innerHTML = `
            <div class="col-12 text-center">
                <p>Không tìm thấy sản phẩm nổi bật</p>
            </div>
        `;
    }
}

/**
 * Create a product card element
 * @param {Object} product - Product data
 * @returns {HTMLElement} - Product card element
 */
function createProductCard(product) {
    const col = document.createElement('div');
    col.className = 'col-md-3';
    
    // Use the image URL directly from product.image
    const imageUrl = product.image || 'assets/img/tech/default-product.jpg';
    
    col.innerHTML = `
        <div class="card h-100 border-3">
            <div class="card-body text-center">
                <img class="img-fluid" src="${imageUrl}" alt="${product.productName}" style="max-height: 200px; width: auto;">
                <p class="card-title mt-3" style="text-align: left;font-size: 20px;font-weight: bold;height: 50px;overflow: hidden;">${product.productName}</p>
                <p class="card-description-1" style="text-align: left;font-size: 18px;font-weight: bold;color: #198754;">${formatPrice(product.price)} VNĐ</p>
                <a href="product-detail-page?product=${product.productId}">
                    <button class="btn btn-outline-success" type="button" style="border-radius: 20px;font-size: 16px;">Mua ngay</button>
                </a>
            </div>
        </div>
    `;
    
    return col;
}

/**
 * Format price with thousand separators
 * @param {number} price - Price to format
 * @returns {string} - Formatted price
 */
function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
}

/**
 * Function to check if an image URL exists
 * @param {string} url - Image URL to check
 * @returns {Promise} - Promise resolving to true if image exists, false otherwise
 */
function checkImageExists(url) {
    return new Promise((resolve) => {
        const img = new Image();
        img.onload = () => resolve(true);
        img.onerror = () => resolve(false);
        img.src = url;
    });
}