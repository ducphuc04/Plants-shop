document.addEventListener("DOMContentLoaded", function() {
    let currentPage = 0;
    let totalPages = 0;
    let username = '';
    let actionOrderId = null;

    // Get JWT token
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    if (!token) {
        Swal.fire({
            title: 'Authentication Required',
            text: 'Please login to view your order history',
            icon: 'info',
            confirmButtonText: 'Login Now'
        }).then((result) => {
            if (result.isConfirmed) {
                localStorage.setItem('returnUrl', '/history-order');
                window.location.href = '/login';
            }
        });
        return;
    }

    // Parse JWT to get username
    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    const decodedToken = parseJwt(token);
    username = decodedToken.sub;
    
    // Format currency
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount) + ' VNĐ';
    }
    
    // Format date
    function formatDate(dateString) {
        if (!dateString) return 'Not Available';
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN');
    }

    // Load orders with pagination
    function loadOrders(page) {
        currentPage = page;
        
        fetch(`/user/get-order?page=${page}&size=5`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            console.log("Response status:", response.status);
            if (!response.ok) {
                throw new Error('Failed to load orders');
            }
            return response.json();
        })
        .then(data => {
            console.log("API Response:", data);
            if (data.result) {
                console.log("API Response1:", data);
                displayOrders(data.result);
                totalPages = data.result.totalPages;
                renderPagination();
            } else{
                console.log("NO result in API response")
                throw new Error('Invalid API response');
            }
        })
        .catch(error => {
            console.log("No result in API response");
            document.getElementById('orders-container').innerHTML = 
                '<div class="alert alert-danger">Failed to load orders. Please try again later.</div>';
        });
    }
    
    // Display orders
    function displayOrders(data) {
        const orders = data.items;
        const container = document.getElementById('orders-container');
        
        container.innerHTML = '';
        
        if (orders.length === 0) {
            container.innerHTML = '<div class="alert alert-info">You have no orders yet.</div>';
            return;
        }
        
        orders.forEach(order => {
            const statusClass = `status-${order.status.toLowerCase()}`;
            const orderDate = formatDate(order.orderDate);
            const paymentDate = order.paymentDate ? formatDate(order.paymentDate) : 'Chưa thanh toán';
            
            let actionButtons = '';
            
            if (order.status === 'unpaid' || order.status === 'canceled') {
                actionButtons = `<button class="btn btn-sm btn-success btn-action btn-pay" data-order-id="${order.orderId}">Thanh toán ngay</button>`;
            } else if (order.status === 'pending') {
                actionButtons = `<button class="btn btn-sm btn-danger btn-action btn-cancel" data-order-id="${order.orderId}">Hủy</button>`;
            }
            
            const orderHtml = `
                <div class="card order-card">
                    <div class="order-header" data-order-id="${order.orderId}">
                        <div class="d-flex flex-column">
                            <div class="mb-2">
                                <span class="fw-bold">Order #${order.orderId}</span>
                                <span class="ms-3 text-muted small">Ordered: ${orderDate}</span>
                            </div>
                            <div class="d-flex flex-wrap">
                                <div class="me-4 mb-2">
                                    <small class="text-muted">Ngày thanh toán:</small>
                                    <div>${paymentDate}</div>
                                </div>
                                <div class="me-4 mb-2">
                                    <small class="text-muted">Tổng tiền:</small>
                                    <div>${formatCurrency(order.totalPrice)}</div>
                                </div>
                                <div class="me-4 mb-2">
                                    <small class="text-muted">Tổng số hàng:</small>
                                    <div>${order.totalProduct}</div>
                                </div>
                                <div class="me-4 mb-2">
                                    <small class="text-muted">Phương thức thanh toán:</small>
                                    <div>${order.paymentMethod || 'Not selected'}</div>
                                </div>
                            </div>
                        </div>
                        <div class="d-flex align-items-center">
                            <span class="order-status ${statusClass} me-3">${order.status}</span>
                            ${actionButtons}
                            <i class="fas fa-chevron-down ms-2 toggle-icon"></i>
                        </div>
                    </div>
                    <div class="order-details" id="order-details-${order.orderId}">
                        <div class="text-center">
                            <div class="spinner-border spinner-border-sm" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            container.innerHTML += orderHtml;
        });
        
        // Attach event listeners after rendering
        attachEventListeners();
    }
    
    // Attach event listeners
    function attachEventListeners() {
        // Order header click (expand/collapse)
        document.querySelectorAll('.order-header').forEach(header => {
            header.addEventListener('click', function() {
                const orderId = this.getAttribute('data-order-id');
                const detailsDiv = document.getElementById(`order-details-${orderId}`);
                const toggleIcon = this.querySelector('.toggle-icon');
                
                if (detailsDiv.classList.contains('active')) {
                    detailsDiv.classList.remove('active');
                    toggleIcon.classList.remove('rotate');
                } else {
                    loadOrderDetails(orderId);
                    detailsDiv.classList.add('active');
                    toggleIcon.classList.add('rotate');
                }
            });
        });
        
        // Pay button click
        document.querySelectorAll('.btn-pay').forEach(button => {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                const orderId = this.getAttribute('data-order-id');
                document.getElementById('paymentOrderId').value = orderId;
                
                // Reset form
                document.getElementById('paymentForm').reset();
                
                // Show modal
                const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));
                paymentModal.show();
            });
        });
        
        // Cancel button click
        document.querySelectorAll('.btn-cancel').forEach(button => {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                actionOrderId = this.getAttribute('data-order-id');
                
                document.getElementById('confirmationTitle').textContent = 'Cancel Order';
                document.getElementById('confirmationMessage').textContent = 'Are you sure you want to cancel this order?';
                
                const confirmModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
                confirmModal.show();
            });
        });
    }
    
    // Load order details
    function loadOrderDetails(orderId) {
        console.log(orderId);
        fetch(`/user/order-details/${orderId}?page=0&size=10`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            console.log("Response status:", response.status);
            if (!response.ok) {
                throw new Error('Failed to load order details');
            }
            return response.json();
        })
        .then(data => {
            console.log("API Response:", data);
            if (data.result) {
                displayOrderDetails(orderId, data.result);
            }
        })
        .catch(error => {
            console.error('Error loading order details:', error);
            document.getElementById(`order-details-${orderId}`).innerHTML = 
                '<div class="alert alert-danger">Failed to load order details.</div>';
        });
    }
    
    // Display order details
    function displayOrderDetails(orderId, data) {
        const items = data.items;
        const detailsContainer = document.getElementById(`order-details-${orderId}`);
        
        let productsHtml = '';
        
        items.forEach(item => {
            productsHtml += `
                <div class="product-item d-flex align-items-center">
                    <img src="${item.imageUrl}" alt="${item.productName}" class="product-image me-3">
                    <div class="flex-grow-1">
                        <div class="fw-bold">${item.productName}</div>
                        <div class="d-flex justify-content-between">
                            <div>
                                <small class="text-muted">Số lượng: ${item.quantity}</small>
                            </div>
                            <div>
                                <small class="text-muted">Giá: ${formatCurrency(item.price)}</small>
                            </div>
                            <div class="fw-bold">
                                ${formatCurrency(item.totalPrice)}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });
        
        const detailsHtml = `
            <div>
                <h6>Danh sách mặt hàng</h6>
                <div class="order-products">
                    ${productsHtml}
                </div>
                <div class="d-flex justify-content-end mt-3">
                    <div class="fw-bold fs-5">Tổng: ${formatCurrency(items && items.length > 0 ? 
                        items.reduce((sum, item) => sum + item.totalPrice, 0) : 0)}</div>
                </div>
            </div>
        `;
        
        detailsContainer.innerHTML = detailsHtml;
    }
    
    // Render pagination
    function renderPagination() {
        const pagination = document.getElementById('pagination');
        pagination.innerHTML = '';
        
        // Previous button
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" ${currentPage > 0 ? 
            `onclick="return false;"` : ''}>Previous</a>`;
        
        if (currentPage > 0) {
            prevLi.querySelector('a').addEventListener('click', function() {
                loadOrders(currentPage - 1);
            });
        }
        
        pagination.appendChild(prevLi);
        
        // Page numbers
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, currentPage + 2);
        
        for (let i = startPage; i <= endPage; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            pageLi.innerHTML = `<a class="page-link" href="#" onclick="return false;">${i + 1}</a>`;
            
            pageLi.querySelector('a').addEventListener('click', function() {
                loadOrders(i);
            });
            
            pagination.appendChild(pageLi);
        }
        
        // Next button
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" ${currentPage < totalPages - 1 ? 
            `onclick="return false;"` : ''}>Next</a>`;
        
        if (currentPage < totalPages - 1) {
            nextLi.querySelector('a').addEventListener('click', function() {
                loadOrders(currentPage + 1);
            });
        }
        
        pagination.appendChild(nextLi);
    }
    
    // Process payment
    function processPayment(orderId, paymentData) {
        fetch(`/user/payment/${username}/${orderId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(paymentData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to process payment');
            }
            return response.json();
        })
        .then(data => {
            Swal.fire({
                title: 'Success',
                text: 'Payment completed successfully',
                icon: 'success',
                confirmButtonText: 'OK'
            }).then(() => {
                // Reload orders to reflect changes
                loadOrders(currentPage);
            });
        })
        .catch(error => {
            console.error('Error processing payment:', error);
            Swal.fire({
                title: 'Error',
                text: 'Failed to process payment. Please try again.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        });
    }
    
    // Cancel order
    function cancelOrder(orderId) {
        fetch(`/user/cancel-order/${orderId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to cancel order');
            }
            return response.json();
        })
        .then(data => {
            Swal.fire({
                title: 'Success',
                text: 'Order cancelled successfully',
                icon: 'success',
                confirmButtonText: 'OK'
            }).then(() => {
                // Reload orders to reflect changes
                loadOrders(currentPage);
            });
        })
        .catch(error => {
            console.error('Error cancelling order:', error);
            Swal.fire({
                title: 'Error',
                text: 'Failed to cancel order. Please try again.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        });
    }

    document.getElementById('submitPayment').addEventListener('click', function() {
        const form = document.getElementById('paymentForm');
        
        if (form.checkValidity()) {
            const orderId = document.getElementById('paymentOrderId').value;
            const paymentData = {
                paymentMethod: document.getElementById('paymentMethod').value,
                name: document.getElementById('name').value,
                address: document.getElementById('address').value,
                phone: document.getElementById('phone').value,
                email: document.getElementById('email').value
            };
            
            const modal = bootstrap.Modal.getInstance(document.getElementById('paymentModal'));
            modal.hide();
            
            processPayment(orderId, paymentData);
        } else {
            form.reportValidity();
        }
    });

    document.getElementById('confirmAction').addEventListener('click', function() {
        if (actionOrderId) {
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('confirmationModal'));
            modal.hide();
            
            // Cancel order
            cancelOrder(actionOrderId);
            actionOrderId = null;
        }
    });
    
    // Initial load
    loadOrders(0);
});