document.addEventListener("DOMContentLoaded", function () {
    console.log("CheckOut.js loaded");

    const orderItemsContainer = document.getElementById("order-items");
    const orderTotalElement = document.getElementById("order-total");
    const orderGrandTotalElement = document.getElementById("order-grand-total");
    const submitOrderButton = document.getElementById("submit-order-btn");
    const qrModal = document.getElementById("qr-modal");
    const closeModalButton = document.getElementById(".close1");
    const bankOption = document.getElementById("bank-option");
    const orderForm = document.getElementById("order-form");

    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get("orderId");

    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    if (!token) {
        Swal.fire({
            title: 'Bạn cần đăng nhập',
            text: 'Vui lòng đăng nhập để tiến hành thanh toán',
            icon: 'info',
            confirmButtonText: 'Đăng nhập ngay'
        }).then((result) => {
            if (result.isConfirmed) {
                localStorage.setItem('returnUrl', '/checkout');
                window.location.href = '/login';
            }
        });
        return;
    }

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    const decodedToken = parseJwt(token);
    const username = decodedToken.sub;

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount) + ' VNĐ';
    }

    if (!orderId) {
        Swal.fire({
            title: 'Lỗi',
            text: 'Không tìm thấy đơn hàng',
            icon: 'error',
            confirmButtonText: 'Quay lại giỏ hàng'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '/shopping-cart';
            }
        });
        return;
    }

    let totalOrderPrice = 0;

    function loadOrderDetails() {
        // Here you would typically fetch the order details from the server
        // For now, we'll use placeholder data

        fetch(`/admin/orderDetail/${orderId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch order details');
                }
                return response.json();
            })
            .then(data => {
                console.log("Order details data:", data);
                const orderDetails = data.result;
                const items = orderDetails.items;
                console.log("Order details items:", items);
                // Clear the container first
                orderItemsContainer.innerHTML = '';

                // Calculate total price
                totalOrderPrice = 0;
                const styleElement = document.createElement('style');
                styleElement.textContent = `
                    .order-item {
                        background-color: #f9f9f9;
                        border-radius: 8px;
                        padding: 10px 15px;
                        margin-bottom: 10px;
                    }
                    
                    .order-item-image {
                        border-radius: 5px;
                    }
                    
                    .product-name {
                        font-weight: 500;
                    }
                    
                    .price-tag {
                        font-weight: 600;
                        color: #74a65d;
                    }
                `;
                document.head.appendChild(styleElement);
                // Add each order detail item to the container
                items.forEach(item => {
                    totalOrderPrice += item.totalPrice;
                    console.log("Order details data3:", data);
                    const itemElement = document.createElement('div');
                    itemElement.className = 'cart-item d-flex justify-content-between align-items-center mb-3';
                    itemElement.innerHTML = `
                        <div class="d-flex align-items-center">
                            <img src="${item.imageUrl}" alt="${item.productName}" 
                                style="width: 60px; height: 60px; object-fit: cover; margin-right: 10px;">
                            <span>${item.productName}</span>
                        </div>
                        <div>
                            ${formatCurrency(item.totalPrice)}
                        </div>
                    `;
                    orderItemsContainer.appendChild(itemElement);

                });
                orderTotalElement.textContent = formatCurrency(totalOrderPrice);
                orderGrandTotalElement.textContent = formatCurrency(totalOrderPrice);

            })
            .catch(error => {
                Swal.fire({
                    title: 'Lỗi',
                    text: 'Không thể tải thông tin đơn hàng',
                    icon: 'error',
                    confirmButtonText: 'Quay lại giỏ hàng'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = '/shopping-cart';
                    }
                });
            });
    }

    function submitOrder() {
        // Get form values
        const name = document.getElementById('name').value;
        const address = document.getElementById('address').value;
        const phone = document.getElementById('phone').value;
        const email = document.getElementById('email').value;
        const paymentMethod = document.querySelector('input[name="payment"]:checked').value;

        // Validate form
        if (!name || !address || !phone) {
            Swal.fire({
                title: 'Lỗi',
                text: 'Vui lòng điền đầy đủ thông tin',
                icon: 'error',
                confirmButtonText: 'Ok'
            });
            return;
        }

        // Create order request
        const orderRequest = {
            name: name,
            address: address,
            phone: phone,
            email: email,
            paymentMethod: paymentMethod
        };

        // Submit order
        fetch(`/user/payment/${username}/${orderId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderRequest)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to submit order');
                }
                return response.json();
            })
            .then(data => {
                if (paymentMethod === 'BANKING') {
                    // Show QR code modal for bank transfer
                    qrModal.style.display = 'block';
                } else {
                    // Show success message for cash payment
                    Swal.fire({
                        title: 'Thành công',
                        text: 'Đơn hàng của bạn đã được đặt thành công',
                        icon: 'success',
                        confirmButtonText: 'Ok'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.href = '/history-order';
                        }
                    });
                }
            })
            .catch(error => {
                console.error('Error submitting order:', error);
                Swal.fire({
                    title: 'Lỗi',
                    text: 'Không thể đặt hàng. Vui lòng thử lại sau',
                    icon: 'error',
                    confirmButtonText: 'Ok'
                });
            });
    }

    if (submitOrderButton) {
        submitOrderButton.addEventListener('click', submitOrder);
    }

    if (closeModalButton) {
        closeModalButton.addEventListener('click', function() {
            qrModal.style.display = 'none';
            window.location.href = '/history-order';
        });
    }

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === qrModal) {
            qrModal.style.display = 'none';
            window.location.href = '/history-order';
        }
    });

    loadOrderDetails();
})