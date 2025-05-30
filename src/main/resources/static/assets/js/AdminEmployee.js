const username ="";

document.addEventListener( 'DOMContentLoaded', function () {
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');
    if (!token){
        Swal.fire({
            icon: 'error',
            title: 'Unauthorized',
            text: 'You need to login to access this page.'
        }).then(() => {
            window.location.href = '/login';
        });
        return;
    }
    const decodedToken = parseJwt(token);
    const username = decodedToken.sub;
    getListEmployee();

    deleteEmployee();

    redirectCreateEmployee();
})

function parseJwt(token) {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
        return null;
    }
}

function getListEmployee(page, size = 10){

    const tableBody = document.getElementById('employees-table-body');
    tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Loading...</td></tr>';

    fetch(`admin/getAllEmployees?page=${page}&size=${size}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
            'Content-Type': 'application/json'
        }
    })
        .then(response =>{
            console.log(response);
            if (!response.ok){
                throw new Error('Failed to fetch employee');
            }
            return response.json();
        })
        .then(data =>{
            console.log(data);
            if (data && data.result){
                if (data.result.items.length > 0) {
                    renderEmployeeTable(data.result.items);
                } else{
                    tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No employees found</td></tr>';
                }

                renderPagination(data.result.page, data.result.totalPages);
            }
        })
        .catch(error => {
            console.error('Error fetching employee:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load employee'
            })
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Error loading data</td></tr>';
        });
}

function renderEmployeeTable(employees) {
    const tableBody = document.getElementById('employees-table-body');
    tableBody.innerHTML = '';

    employees.forEach(employee => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${employee.fullName}</td>
            <td>${employee.role}</td>
            <td>${employee.address}</td>
            <td>${employee.phone}</td>
            <td>${employee.totalOrder}</td>
            <td>
                <button class="btn btn-danger delete-employee" data-id="${employee.employeeId}" onclick=deleteEmployee(${employee.employeeId})>Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function deleteEmployee(employeeId) {

    const tableBody = document.getElementById('employees-table-body');
    tableBody.addEventListener('click', function (event) {
        if (event.target.classList.contains('delete-employee')) {
            const employeeId = event.target.getAttribute('data-id');
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
                    fetch(`admin/deleteEmployee/${employeeId}`, {
                        method: 'DELETE',
                        headers: {
                            'Authorization': 'Bearer ' + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
                            'Content-Type': 'application/json'
                        }
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Failed to delete employee');
                            }
                            return response.json();
                        })
                        .then(data => {
                            Swal.fire(
                                'Deleted!',
                                'Employee has been deleted.',
                                'success'
                            );
                            getListEmployee();
                        })
                        .catch(error => {
                            console.error('Error deleting employee:', error);
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'Failed to delete employee'
                            });
                        });
                }
            });
        }
    });
}

function redirectCreateEmployee(){
    const actionButton = document.getElementById('add-employee');
    actionButton.addEventListener('click', function (){
        window.location.href = '/admin/create-employee';
    });
}

function renderPagination(currentPage, totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    if (totalPages <= 1) {
        return;
    }

    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    prevLi.innerHTML = '<a class="page-link" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a>';
    if (currentPage > 0) {
        prevLi.addEventListener('click', () => loadUsersWithOrders(currentPage - 1));
    }
    pagination.appendChild(prevLi);

    for (let i = 0; i < totalPages; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        pageLi.addEventListener('click', () => loadUsersWithOrders(i));
        pagination.appendChild(pageLi);
    }

    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    nextLi.innerHTML = '<a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>';
    if (currentPage < totalPages - 1) {
        nextLi.addEventListener('click', () => loadUsersWithOrders(currentPage + 1));
    }
    pagination.appendChild(nextLi);
}