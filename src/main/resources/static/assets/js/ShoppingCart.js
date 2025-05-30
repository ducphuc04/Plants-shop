document.addEventListener("DOMContentLoaded", function () {
    console.log("ShoppingCart.js loaded");

    // DOM elements
    const cartItemsContainer = document.getElementById('cart-items-container');
    const emptyCartMessage = document.getElementById('empty-cart-message');
    const cartTotalSection = document.getElementById('cart-total-section');
    const subtotalElement = document.getElementById('sub-total');
    const grandTotalElement = document.getElementById('grand-total');
    const checkoutButton = document.getElementById('checkout-btn');
    const createOrderButton = document.getElementById('create-order-btn')

    // Get JWT token
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    if (!token) {
        Swal.fire({
            title: 'Bạn cần đăng nhập',
            text: 'Vui lòng đăng nhập để xem giỏ hàng',
            icon: 'info',
            confirmButtonText: 'Đăng nhập ngay'
        }).then((result) => {
            if (result.isConfirmed) {
                localStorage.setItem('returnUrl', '/shopping-cart');
                window.location.href = '/login';
            }
        });
        return;
    }

    // Parse JWT token to get username
    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    const decodedToken = parseJwt(token);
    const username = decodedToken.sub;

    // Format currency
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount) + ' VNĐ';
    }

    // Load cart items
    function loadCartItems() {
        fetch(`/user/get-cart/${username}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch cart items');
                }
                return response.json();
            })
            .then(data => {
                const cartItems = data.result;

                if (!cartItems || cartItems.length === 0) {
                    showEmptyCart();
                    return;
                }

                renderCartItems(cartItems);
                updateTotals(cartItems);
                attachEventListeners();
            })
            .catch(error => {
                console.error('Error loading cart items:', error);
                showEmptyCart();
            });
    }

    // Show empty cart message
    function showEmptyCart() {
        cartItemsContainer.innerHTML = '';
        emptyCartMessage.style.display = 'block';
        cartTotalSection.style.display = 'none';
    }

    // Render cart items
    function renderCartItems(cartItems) {
        cartItemsContainer.innerHTML = '';
        emptyCartMessage.style.display = 'none';
        cartTotalSection.style.display = 'block';

        cartItems.forEach(item => {
            const itemElement = document.createElement('div');
            itemElement.className = 'cart-item';
            itemElement.innerHTML = `
                <div class="product">
                    <img src="${item.image}" width="80" height="63" alt="Product image">
                    <div class="item-detail">
                        <p>${item.productName}</p>
                        <div class="size-color-box"></div>
                    </div>
                </div>
                <span class="price">${formatCurrency(item.price / item.quantity)}</span>
                <div class="quantity">
                    <input type="number" value="${item.quantity}" min="1" 
                           data-product-id="${item.productId}" 
                           class="quantity-input" 
                           data-original-quantity="${item.quantity}">
                </div>
                <span class="total-price">${formatCurrency(item.price)}</span>
                <button class="btn btn-sm btn-danger delete-btn" data-product-id="${item.productId}">
                    <i class="fas fa-trash"></i>
                </button>
            `;

            cartItemsContainer.appendChild(itemElement);
        });
    }

    // Calculate and update totals
    function updateTotals(cartItems) {
        let totalPrice = 0;

        cartItems.forEach(item => {
            totalPrice += item.price;
        });

        subtotalElement.textContent = formatCurrency(totalPrice);
        grandTotalElement.textContent = formatCurrency(totalPrice);
    }

    // Attach event listeners to buttons and inputs
    function attachEventListeners() {
        // Quantity input event listeners
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.addEventListener('change', function() {
                const productId = this.getAttribute('data-product-id');
                const newQuantity = parseInt(this.value);
                const originalQuantity = parseInt(this.getAttribute('data-original-quantity'));

                if (newQuantity <= 0) {
                    this.value = originalQuantity;
                    Swal.fire({
                        icon: 'error',
                        title: 'Lỗi',
                        text: 'Số lượng phải lớn hơn 0'
                    });
                    return;
                }

                updateCartItem(username, productId, newQuantity);
            });
        });

        // Delete button event listeners
        document.querySelectorAll('.delete-btn').forEach(button => {
            button.addEventListener('click', function() {
                const productId = this.getAttribute('data-product-id');

                Swal.fire({
                    title: 'Xác nhận',
                    text: 'Bạn có muốn xóa sản phẩm này không?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Đồng ý',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        deleteCartItem(username, productId);
                    }
                });
            });
        });

        // Checkout button event listener
        if (checkoutButton) {
            checkoutButton.addEventListener('click', function() {
                createOrder(username);
            });
        }
    }

    // Update cart item quantity
    function updateCartItem(username, productId, newQuantity) {
        fetch(`/user/update-cart/${username}/${productId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(newQuantity)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to update cart item');
                }
                return response.json();
            })
            .then(data => {
                loadCartItems();
            })
            .catch(error => {
                console.error('Error updating cart item:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Cập nhật số lượng thất bại'
                });
            });
    }

    // Delete cart item
    function deleteCartItem(username, productId) {
        fetch(`/user/delete-cart-item/${username}/${productId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to delete cart item');
                }
                return response.json();
            })
            .then(data => {
                if (data.result) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Thành công',
                        text: 'Xóa thành công sản phẩm',
                        showConfirmButton: false,
                        timer: 1500
                    }).then(() => {
                        loadCartItems(); // Reload cart items
                    });
                }
            })
            .catch(error => {
                console.error('Error deleting cart item:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Xóa sản phẩm thất bại'
                });
            });
    }

    // Create order
    function createOrder(username) {
        fetch(`/user/createOrder/${username}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to create order');
                }
                return response.json();
            })
            .then(data => {
                if (data.result) {
                    const orderId = data.result.orderId;
                    console.log(orderId);
                    if (orderId) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Thành công',
                            text: 'Đơn hàng đã được tạo',
                            showConfirmButton: false,
                            timer: 1500
                        }).then(() => {
                            window.location.href = `/checkout?orderId=${orderId}`;
                        });
                    } else {
                        throw new Error("Failed to create order");
                    }
                }else{
                    throw new Error('Invalid response format');
                }
            })
            .catch(error => {
                console.error('Error creating order:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Tạo đơn hàng thất bại. ' + error.message
                });
            });
    }

    loadCartItems();
});