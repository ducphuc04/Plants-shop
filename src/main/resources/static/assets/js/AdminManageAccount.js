document.addEventListener('DOMContentLoaded', function() {
    // Fetch employee info when page loads
    fetchEmployeeInfo();
    
    // Add event listener to the manage account button
    document.getElementById('manageAccountBtn').addEventListener('click', updateEmployeeInfo);
});

// Function to fetch employee information
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
        if (data && data.result) {
            // Display employee information in the form
            document.querySelector('tr:nth-child(1) td:nth-child(2) span').textContent = data.result.username || '';
            document.querySelector('tr:nth-child(3) td:nth-child(2) span').textContent = data.result.role || '';
            
            // Set values for editable fields
            document.querySelector('tr:nth-child(4) td:nth-child(2) input').value = data.result.address || '';
            document.querySelector('tr:nth-child(5) td:nth-child(2) input').value = data.result.phone || '';
            
            // Update welcome text
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

// Function to update employee information
function updateEmployeeInfo() {
    // Get values from form
    const address = document.querySelector('tr:nth-child(4) td:nth-child(2) input').value;
    const phone = document.querySelector('tr:nth-child(5) td:nth-child(2) input').value;
    const newPassword = document.querySelector('tr:nth-child(6) td:nth-child(2) input').value;
    const confirmPassword = document.querySelector('tr:nth-child(7) td:nth-child(2) input').value;
    const oldPassword = document.querySelector('tr:nth-child(2) td:nth-child(2) input').value;
    
    // Create request object
    const updateData = {};
    
    // Only include fields with values
    if (address) updateData.address = address;
    if (phone) updateData.phone = phone;
    
    // Handle password update if new password is provided
    if (newPassword) {
        if (!oldPassword) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Please enter your current password to change password'
            });
            return;
        }
        
        if (newPassword !== confirmPassword) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'New password and confirmation do not match'
            });
            return;
        }
        
        updateData.oldPassword = oldPassword;
        updateData.newPassword = newPassword;
        updateData.confirmPassword = confirmPassword;
    }
    
    // Get username from the displayed value
    const username = document.querySelector('tr:nth-child(1) td:nth-child(2) span').textContent;
    
    // Send update request
    fetch(`/admin/updateEmployee/${username}`, {
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update employee information');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.result) {
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: 'Your account information has been updated successfully!'
            });
            
            // Clear password fields
            document.querySelector('tr:nth-child(2) td:nth-child(2) input').value = '';
            document.querySelector('tr:nth-child(6) td:nth-child(2) input').value = '';
            document.querySelector('tr:nth-child(7) td:nth-child(2) input').value = '';
            
            // Refresh employee information
            fetchEmployeeInfo();
        }
    })
    .catch(error => {
        console.error('Error updating employee info:', error);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to update account information'
        });
    });
}