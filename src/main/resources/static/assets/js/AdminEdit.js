// Admin Edit Product JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Get product ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('productId');
    
    if (!productId) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No product ID specified',
            confirmButtonColor: '#c4c8cb'
        });
        return;
    }
    
    // Load product details
    loadProductDetails(productId);
    
    // Add event listener to the edit button
    document.getElementById('editProductBtn').addEventListener('click', function() {
        updateProduct(productId);
    });
    
    // Setup image preview functionality
    const uploadImage = document.getElementById('uploadImage');
    const imagePreview = document.getElementById('imagePreview');
    
    uploadImage.addEventListener('change', function() {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                imagePreview.classList.remove('d-none');
            };
            reader.readAsDataURL(this.files[0]);
        }
    });
});

function loadProductDetails(productId) {
    fetch(`/product/detailProduct/${productId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.result) {
                const product = data.result;
                
                // Fill in the form fields
                const inputs = document.querySelectorAll('table.custom-table input');
                
                // Product name (readonly)
                inputs[0].value = product.productName;
                
                // Category (readonly)
                inputs[1].value = product.category;
                
                // Price (editable)
                inputs[2].value = product.price;
                
                // Stock (editable)
                inputs[3].value = product.stock;
                
                // Description (editable)
                inputs[4].value = product.description;
                
                // Set image preview if available
                if (product.image) {
                    const imagePreview = document.getElementById('imagePreview');
                    imagePreview.src = product.image;
                    imagePreview.classList.remove('d-none');
                }
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: data.message || 'Failed to load product details',
                    confirmButtonColor: '#c4c8cb'
                });
            }
        })
        .catch(error => {
            console.error('Error fetching product details:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load product details',
                confirmButtonColor: '#c4c8cb'
            });
        });
}

function updateProduct(productId) {

    const inputs = document.querySelectorAll('table.custom-table input');
    const price = parseInt(inputs[2].value);
    const stock = parseInt(inputs[3].value);
    const description = inputs[4].value;

    if (isNaN(price) || price <= 0) {
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: 'Price must be a positive number',
            confirmButtonColor: '#c4c8cb'
        });
        return;
    }
    
    if (isNaN(stock) || stock < 0) {
        Swal.fire({
            icon: 'error',
            title: 'Validation Error',
            text: 'Stock must be a non-negative number',
            confirmButtonColor: '#c4c8cb'
        });
        return;
    }

    const productData = {
        price: price,
        stock: stock,
        description: description
    };

    fetch(`/product/updateProduct/${productId}`, {
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(productData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        if (data.result) {
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: 'Product updated successfully',
                confirmButtonColor: '#c4c8cb'
            }).then(() => {
                // Redirect to product list page
                window.location.href = 'admin-product';
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: data.message || 'Failed to update product',
                confirmButtonColor: '#c4c8cb'
            });
        }
    })
    .catch(error => {
        console.error('Error updating product:', error);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to update product',
            confirmButtonColor: '#c4c8cb'
        });
    });
}