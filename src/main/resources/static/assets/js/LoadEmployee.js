document.addEventListener('DOMContentLoaded', function() {
    fetchEmployeeInfo();
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
