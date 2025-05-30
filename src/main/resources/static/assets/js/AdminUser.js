document.addEventListener('DOMContentLoaded', function() {
    fetchEmployeeInfo();
    
    loadUsersWithOrders(0);
});

function fetchEmployeeInfo() {
    fetch('/admin/getEmployee', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch employee info');
            }
            return response.json();
        })
        .then(data => {
            console.log(data.result);
            console.log(data.result.fullName);
            if (data && data.result) {
                document.getElementById('welcome-text').textContent = `Welcome, ${data.result.fullName}`;
            }
        })
        .catch(error => {
            console.error('Error fetching employee info:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load employee information'
            });
        });
}

function loadUsersWithOrders(page, size = 10) {
    // Show loading state
    const tableBody = document.getElementById('users-table-body');
    tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Loading...</td></tr>';
    
    // Call the existing API endpoint
    fetch(`/admin/listUser?page=${page}&size=${size}`,{
        method: 'GET',
        headers:{
            'Authorization': 'Bearer ' + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch users');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.result) {
                console.log(data.result);
                const usersWithOrders = data.result.items.filter(user => user.totalProducts > 0);
                console.log("******" + usersWithOrders.length);
                if (data.result.items.length > 0) {
                    renderUsersTable(data.result.items);
                } else {
                    tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No users with orders found</td></tr>';
                }
                
                renderPagination(data.result.page, data.result.totalPages);
            }
        })
        .catch(error => {
            console.error('Error fetching users:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load users with orders'
            });
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Error loading data</td></tr>';
        });
}

function renderUsersTable(users) {
    const tableBody = document.getElementById('users-table-body');
    tableBody.innerHTML = '';
    
    users.forEach(user => {
        const row = document.createElement('tr');
        
        row.innerHTML = `
            <td>${user.username}</td>
            <td>${user.name}</td>
            <td style="text-align: center;">${user.totalProduct}</td>
            <td style="text-align: center;">${user.totalPrice}</td>
            <td style="text-align: center;">${user.phone}</td>
        `;
        
        tableBody.appendChild(row);
    });
}

function renderPagination(currentPage, totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    // Only show pagination if there are multiple pages
    if (totalPages <= 1) {
        return;
    }
    
    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    prevLi.innerHTML = '<a class="page-link" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a>';
    if (currentPage > 0) {
        prevLi.addEventListener('click', () => loadUsersWithOrders(currentPage - 1));
    }
    pagination.appendChild(prevLi);
    
    // Page numbers
    for (let i = 0; i < totalPages; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        pageLi.addEventListener('click', () => loadUsersWithOrders(i));
        pagination.appendChild(pageLi);
    }
    
    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    nextLi.innerHTML = '<a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>';
    if (currentPage < totalPages - 1) {
        nextLi.addEventListener('click', () => loadUsersWithOrders(currentPage + 1));
    }
    pagination.appendChild(nextLi);
}