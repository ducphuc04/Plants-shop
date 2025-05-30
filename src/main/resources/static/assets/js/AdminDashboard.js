document.addEventListener("DOMContentLoaded", function() {
    // Get JWT token
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Format currency
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount) + ' VNĐ';
    }

    let revenueChart; // Define chart variable globally in the scope

    // Load dashboard summary
    function loadDashboardSummary() {
        fetch('/admin/dashboard/summary', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load dashboard summary');
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            if (data.result) {
                document.getElementById('total-products').textContent = data.result.totalProducts;
                document.getElementById('total-income').textContent = formatCurrency(data.result.totalIncome);
                document.getElementById('average-order').textContent = formatCurrency(data.result.averageOrderValue);
            }
        })
        .catch(error => {
            console.error('Error loading dashboard summary:', error);
        });
    }

    // Load daily revenue chart
    function loadDailyRevenueChart() {
        fetch('/admin/dashboard/daily-revenue', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load daily revenue');
            }
            return response.json();
        })
        .then(data => {
            if (data.result) {
                // Process data for the chart
                const chartLabels = [];
                const chartData = [];
                
                // Sort data chronologically
                data.result.sort((a, b) => {
                    return new Date(a.date) - new Date(b.date);
                });
                
                // Extract labels and revenue data
                data.result.forEach(item => {
                    // Format date as DD/MM
                    const date = new Date(item.date);
                    const formattedDate = `${date.getDate()}/${date.getMonth() + 1}`;
                    
                    chartLabels.push(formattedDate);
                    chartData.push(item.total);
                });
                
                // Get the canvas element
                const ctx = document.getElementById('revenue-chart').getContext('2d');
                
                // Destroy previous chart instance if it exists
                if (revenueChart) {
                    revenueChart.destroy();
                }
                
                // Create new chart
                revenueChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: chartLabels,
                        datasets: [{
                            label: 'Daily Revenue (VNĐ)',
                            data: chartData,
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 2,
                            pointBackgroundColor: 'rgba(75, 192, 192, 1)',
                            tension: 0.3
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
                                    }
                                }
                            }
                        },
                        plugins: {
                            title: {
                                display: true,
                                text: 'Daily Revenue (Last 30 Days)',
                                font: {
                                    size: 16
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return 'Revenue: ' + new Intl.NumberFormat('vi-VN').format(context.raw) + ' đ';
                                    }
                                }
                            }
                        }
                    }
                });
            }
        })
        .catch(error => {
            console.error('Error loading daily revenue:', error);
        });
    }

    function loadUserInfo() {
        fetch('/admin/getEmployee', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load user info');
            }
            return response.json();
        })
        .then(data => {
            if (data.result && data.result.username) {
                document.getElementById('welcome-user').textContent = `Welcome, ${data.result.username}`;
            }
        })
        .catch(error => {
            console.error('Error loading user info:', error);
            // Fallback to using JWT subject if API call fails
            try {
                const decodedToken = parseJwt(token);
                if (decodedToken && decodedToken.sub) {
                    document.getElementById('welcome-user').textContent = `Welcome, ${decodedToken.sub}`;
                }
            } catch (e) {
                console.error('Error parsing JWT token:', e);
            }
        });
    }

    // Helper function to parse JWT
    function parseJwt(token) {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    }

    function loadBestSellingProducts() {
        fetch('/admin/dashboard/best-sellers', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load best sellers');
            }
            return response.json();
        })
        .then(data => {
            if (data.result) {
                const tableBody = document.getElementById('best-sellers-table');
                tableBody.innerHTML = '';
                
                data.result.forEach(product => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${product.productName}</td>
                        <td>${product.totalSales}</td>
                        <td>${formatCurrency(product.totalPrice)}</td>
                        <td>${product.currentStock}</td>
                    `;
                    tableBody.appendChild(row);
                });
            }
        })
        .catch(error => {
            console.error('Error loading best sellers:', error);
        });
    }

    // Initialize dashboard
    function initDashboard() {
        loadDashboardSummary();
        loadDailyRevenueChart(); // Changed from loadMonthlyRevenueChart
        loadBestSellingProducts();
        loadUserInfo();
    }

    // Load dashboard data
    initDashboard();
});