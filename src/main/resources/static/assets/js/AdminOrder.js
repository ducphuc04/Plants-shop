document.addEventListener('DOMContentLoaded', function() {
    const ordersContainer = document.getElementById('orders-container');
    const paginationContainer = document.getElementById('pagination');
    const loadingIndicator = document.getElementById('loading-indicator');
    let currentPage = 0;
    let totalPages = 0;
    let currentFilter = 'all';
    
    // Function to format date
    function formatDate(dateString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return new Date(dateString).toLocaleDateString('en-US', options);
    }
    
    // Function to get status background color
    function getStatusBgColor(status) {
        switch(status.toLowerCase()) {
            case 'pending': return 'status-pending';
            case 'processed': return 'status-processed';
            case 'canceled': return 'status-canceled';
            default: return '';
        }
    }
    
    // Function to fetch orders from the server
    async function fetchOrders(page = 0, size = 10) {
        try {
            loadingIndicator.style.display = 'block';

            // Building the API URL with pagination parameters
            let url = `/admin/getOrders?page=${page}&size=${size}`;
        
            // Add filter parameter if not 'all'
            if (currentFilter !== 'all') {
                url += `&status=${currentFilter}`;
            }

            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token'))
                }
            });

            if (!response.ok) {
                throw new Error('Failed to fetch orders');
            }

            const data = await response.json();
            return data.result;
        } catch (error) {
            console.error('Error fetching orders:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load orders. Please try again later.'
            });
            return { content: [], totalPages: 0, number: 0, totalElements: 0 };
        } finally {
            loadingIndicator.style.display = 'none';
        }
    }
    
    // Function to display orders
    function displayOrders(orders) {
        ordersContainer.innerHTML = '';
        
        if (orders.length === 0) {
            ordersContainer.innerHTML = '<div class="text-center py-5"><p>No orders found.</p></div>';
            return;
        }
        
        orders.forEach(order => {
            const orderCard = document.createElement('div');
            orderCard.className = 'card order-card';
            orderCard.dataset.orderId = order.orderId;
            
            const statusClass = getStatusBgColor(order.status);
            
            orderCard.innerHTML = `
                <div class="order-header" data-bs-toggle="collapse" data-bs-target="#order-${order.orderId}">
                    <div>
                        <span class="fw-bold">Order #${order.orderId}</span>
                        <span class="ms-3 text-muted">${formatDate(order.orderDate)}</span>
                    </div>
                    <div class="d-flex align-items-center">
                        <span class="me-3">₫${order.totalPrice.toLocaleString()}</span>
                        <span class="order-status ${statusClass}">${order.status}</span>
                        <i class="fas fa-chevron-down ms-3 toggle-icon"></i>
                    </div>
                </div>
                <div id="order-${order.orderId}" class="order-details">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <p><strong>Payment Method:</strong> ${order.paymentMethod || 'N/A'}</p>
                            <p><strong>Total Products:</strong> ${order.totalProduct}</p>
                            <p><strong>Name:</strong> ${order.name ? order.name : 'No data'}</p>
                            <p><strong>Email:</strong> ${order.email}</p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Order Date:</strong> ${formatDate(order.orderDate)}</p>
                            <p><strong>Payment Date:</strong> ${order.paymentDate ? formatDate(order.paymentDate) : 'Not paid yet'}</p>
                            <p><strong>Phone:</strong> ${order.phone}</p>
                        </div>
                        <p><strong>Address:</strong> ${order.address}</p>
                    </div>
                    
                    <div class="order-products">
                        <h6>Order Items</h6>
                        <div class="text-center" id="loading-items-${order.orderId}">
                            <div class="spinner-border spinner-border-sm" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="mt-2">Loading items...</p>
                        </div>
                        <div id="order-items-${order.orderId}"></div>
                    </div>
                    
                    ${order.status === 'pending' ? `
                    <div class="d-flex justify-content-end mt-3">
                        <button class="btn btn-danger btn-sm btn-action cancel-order" data-order-id="${order.orderId}">Cancel Order</button>
                        <button class="btn btn-success btn-sm btn-action process-order" data-order-id="${order.orderId}">Process Order</button>
                    </div>
                    ` : ''}
                </div>
            `;
            
            ordersContainer.appendChild(orderCard);
        });
        
        // Add event listeners to order headers
        document.querySelectorAll('.order-header').forEach(header => {
            header.addEventListener('click', function() {
                const icon = this.querySelector('.toggle-icon');
                icon.classList.toggle('rotate');
                
                const orderDetails = this.nextElementSibling;
                orderDetails.classList.toggle('active');
                
                // Load order details if they're not already loaded
                if (orderDetails.classList.contains('active')) {
                    const orderId = this.closest('.order-card').dataset.orderId;
                    loadOrderDetails(orderId);
                }
            });
        });
        
        // Add event listeners to action buttons
        document.querySelectorAll('.cancel-order').forEach(button => {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                const orderId = this.dataset.orderId;
                showConfirmationModal('Cancel Order', 'Are you sure you want to cancel this order?', () => {
                    cancelOrder(orderId);
                });
            });
        });
        
        document.querySelectorAll('.process-order').forEach(button => {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                const orderId = this.dataset.orderId;
                showConfirmationModal('Process Order', 'Are you sure you want to process this order?', () => {
                    processOrder(orderId);
                });
            });
        });
    }
    
    // Function to load order details
    async function loadOrderDetails(orderId) {
        const orderItemsContainer = document.getElementById(`order-items-${orderId}`);
        const loadingIndicator = document.getElementById(`loading-items-${orderId}`);
        
        if (orderItemsContainer.innerHTML !== '') {
            return; // Details already loaded
        }
        
        try {
            loadingIndicator.style.display = 'block';
            
            const response = await fetch(`admin/orderDetail/${orderId}?page=0&size=10`,{
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token'))
                }

            });
            if (!response.ok) {
                throw new Error('Failed to fetch order details');
            }
            
            const data = await response.json();
            const orderDetails = data.result.items;
            
            if (orderDetails.length === 0) {
                orderItemsContainer.innerHTML = '<p>No items found for this order.</p>';
                return;
            }
            
            let detailsHtml = '<div class="table-responsive"><table class="table table-sm">';
            detailsHtml += `
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Price</th>
                        <th>Quantity</th>
                        <th>Total</th>
                    </tr>
                </thead>
                <tbody>
            `;
            
            let total = 0;
            orderDetails.forEach(item => {
                const itemTotal = item.price * item.quantity;
                total += itemTotal;
                
                detailsHtml += `
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="${item.imageUrl}" alt="${item.productName}" class="product-image me-2">
                                <span>${item.productName}</span>
                            </div>
                        </td>
                        <td>₫${item.price.toLocaleString()}</td>
                        <td>${item.quantity}</td>
                        <td>₫${itemTotal.toLocaleString()}</td>
                    </tr>
                `;
            });
            
            detailsHtml += `
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" class="text-end"><strong>Total:</strong></td>
                        <td>₫${total.toLocaleString()}</td>
                    </tr>
                </tfoot>
            `;
            detailsHtml += '</table></div>';
            
            orderItemsContainer.innerHTML = detailsHtml;
        } catch (error) {
            console.error('Error loading order details:', error);
            orderItemsContainer.innerHTML = '<p class="text-danger">Failed to load order details. Please try again.</p>';
        } finally {
            loadingIndicator.style.display = 'none';
        }
    }
    
    // Function to create pagination
    function createPagination(currentPage, totalPages) {
        paginationContainer.innerHTML = '';
        
        if (totalPages <= 1) {
            return;
        }
        
        // Previous button
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>`;
        paginationContainer.appendChild(prevLi);
        
        // Page numbers
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, currentPage + 2);
        
        for (let i = startPage; i <= endPage; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            pageLi.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i + 1}</a>`;
            paginationContainer.appendChild(pageLi);
        }
        
        // Next button
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>`;
        paginationContainer.appendChild(nextLi);
        
        // Add event listeners to pagination links
        document.querySelectorAll('.page-link').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.dataset.page);
                if (page >= 0 && page < totalPages) {
                    loadOrders(page);
                }
            });
        });
    }
    
    // Function to show confirmation modal
    function showConfirmationModal(title, message, confirmCallback) {
        const modal = document.getElementById('confirmationModal');
        const modalTitle = document.getElementById('confirmationTitle');
        const modalMessage = document.getElementById('confirmationMessage');
        const confirmButton = document.getElementById('confirmAction');
        
        modalTitle.textContent = title;
        modalMessage.textContent = message;
        
        // Remove previous event listeners
        const newConfirmButton = confirmButton.cloneNode(true);
        confirmButton.parentNode.replaceChild(newConfirmButton, confirmButton);
        
        // Add new event listener
        newConfirmButton.addEventListener('click', function() {
            confirmCallback();
            bootstrap.Modal.getInstance(modal).hide();
        });
        
        // Show the modal
        new bootstrap.Modal(modal).show();
    }
    
    // Function to cancel an order
    async function cancelOrder(orderId) {
        try {
            const response = await fetch(`/admin/solveOrder/${orderId}?action=cancel`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token')),
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error('Failed to cancel order');
            }
            
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: 'Order has been cancelled successfully.'
            }).then(() => {
                // Reload the current page of orders
                loadOrders(currentPage);
            });
        } catch (error) {
            console.error('Error cancelling order:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to cancel order. Please try again.'
            });
        }
    }
    
    // Function to process an order
    async function processOrder(orderId) {
        try {
            const response = await fetch(`/admin/solveOrder/${orderId}?action=process`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + (localStorage.getItem('jwt') || localStorage.getItem('access_token')),
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error('Failed to process order');
            }
            
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: 'Order has been processed successfully.'
            }).then(() => {
                // Reload the current page of orders
                loadOrders(currentPage);
            });
        } catch (error) {
            console.error('Error processing order:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to process order. Please try again.'
            });
        }
    }
    
    // Function to load orders
    async function loadOrders(page = 0) {
        currentPage = page;
        const data = await fetchOrders(page);

        console.log("API Response:", data);
        if (data) {

            displayOrders(data.items);
            totalPages = data.totalPages;
            createPagination(data.number, data.totalPages);
        }
    }
    
    // Event listeners for filter buttons
    document.querySelectorAll('.status-filter').forEach(button => {
        button.addEventListener('click', function() {
            // Update active button
            document.querySelectorAll('.status-filter').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');
            
            // Apply filter
            currentFilter = this.dataset.status;
            loadOrders(0); // Reset to first page when filter changes
        });
    });
    
    
    // Initialize by loading the first page of orders
    loadOrders();
});