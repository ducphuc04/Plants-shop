document.addEventListener('DOMContentLoaded', function() {
    // Load categories when page loads
    loadCategories();
    
    // Handle image preview
    const uploadImage = document.getElementById('uploadImage');
    const imagePreview = document.getElementById('imagePreview');
    
    uploadImage.addEventListener('change', function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                imagePreview.classList.remove('d-none');
            }
            reader.readAsDataURL(file);
        }
    });
    
    // Add product button click handler
    const addProductBtn = document.getElementById('addProductBtn');
    addProductBtn.addEventListener('click', function() {
        if (validateForm()) {
            createProduct();
        }
    });
    
    // Get token from localStorage or sessionStorage
    function getToken() {
        return localStorage.getItem('jwt') || sessionStorage.getItem('access_token');
    }
    
    // Load categories from API
    function loadCategories() {
        fetch('/product/category', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('Failed to load categories');
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            if (data.result && Array.isArray(data.result)) {
                populateCategoryDropdown(data.result);
                
                // Set default category if provided in URL
                const urlParams = new URLSearchParams(window.location.search);
                const categoryParam = urlParams.get('category');
                if (categoryParam) {
                    const categorySelect = document.getElementById('productCategory');
                    for (let i = 0; i < categorySelect.options.length; i++) {
                        if (categorySelect.options[i].value === categoryParam) {
                            categorySelect.selectedIndex = i;
                            break;
                        }
                    }
                }
            }
        })
        .catch(error => {
            console.error('Error loading categories:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load categories. Please try again later.'
            });
        });
    }
    
    // Populate category dropdown
    function populateCategoryDropdown(categories) {
        const categorySelect = document.getElementById('productCategory');
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categorySelect.appendChild(option);
        });
    }
    
    // Validate form
    function validateForm() {
        const productNameInput = document.querySelector("input[placeholder='Enter Product Name']");
        const productPriceInput = document.querySelector("input[placeholder='Enter Price']");
        const productStockInput = document.querySelector("input[placeholder='Enter Stock']");
        const descriptionTextarea = document.querySelector("textarea[placeholder='Enter Description']");
        const categorySelect = document.getElementById('productCategory');
        const uploadImageInput = document.getElementById('uploadImage');

        const productName = productNameInput ? productNameInput.value.trim() : '';
        const productPrice = productPriceInput ? productPriceInput.value.trim() : '';
        const productStock = productStockInput ? productStockInput.value.trim() : '';
        const productDescription = descriptionTextarea ? descriptionTextarea.value.trim() : '';
        const productCategory = categorySelect ? categorySelect.value : '';

        if (!productName) {
            showError('Product name is required');
            return false;
        }
        
        if (!productPrice || isNaN(productPrice) || parseInt(productPrice) <= 0) {
            showError('Please enter a valid price');
            return false;
        }
        
        if (!productStock || isNaN(productStock) || parseInt(productStock) < 0) {
            showError('Please enter a valid stock quantity');
            return false;
        }
        
        if (!productDescription) {
            showError('Product description is required');
            return false;
        }
        
        if (!productCategory) {
            showError('Please select a category');
            return false;
        }
        
        if (!uploadImage.files || uploadImage.files.length === 0) {
            showError('Please select an image for the product');
            return false;
        }
        
        return true;
    }
    
    // Show error message
    function showError(message) {
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: message
        });
    }
    
    // Create product
    function createProduct() {
        const productNameInput = document.querySelector("input[placeholder='Enter Product Name']");
        const productPriceInput = document.querySelector("input[placeholder='Enter Price']");
        const productStockInput = document.querySelector("input[placeholder='Enter Stock']");
        const descriptionTextarea = document.querySelector("textarea[placeholder='Enter Description']");
        const categorySelect = document.getElementById('productCategory');
        const uploadImageInput = document.getElementById('uploadImage');

        if (!productNameInput || !productPriceInput || !productStockInput ||
            !descriptionTextarea || !categorySelect || !uploadImageInput) {
            showError('Some form elements are missing');
            return;
        }

        const productName = productNameInput.value.trim();
        const productPrice = productPriceInput.value.trim();
        const productStock = productStockInput.value.trim();
        const productDescription = descriptionTextarea.value.trim();
        const productCategory = categorySelect.value;
        const uploadImage = uploadImageInput.files[0];

        const formData = new FormData();
        formData.append('name', productName);
        formData.append('price', productPrice);
        formData.append('stock', productStock);
        formData.append('des', productDescription);
        formData.append('category', productCategory);
        formData.append('image', uploadImage);

        // Show loading indicator
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: 'Creating product...',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
        }


        fetch('/product/createProduct', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + getToken()
            },
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create product');
            }
            return response.json();
        })
        .then(data => {
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: 'Product created successfully!'
            }).then(() => {
                // Redirect to product list page
                window.location.href = '/admin-product';
            });
        })
        .catch(error => {
            console.error('Error creating product:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to create product. Please try again.'
            });
        });
    }
});