document.addEventListener("DOMContentLoaded", function (){
    const createEmployeeBtn = document.getElementById("createAccountBtn");
    if (createEmployeeBtn) {
        createEmployeeBtn.addEventListener("click", function () {
            createEmployee();
        });
    }
})

function createEmployee() {
    const usernameInput = document.querySelector("input[placeholder='Enter Username']");
    const passwordInput = document.querySelector("input[placeholder='Enter Password']");
    const fullNameInput = document.querySelector("input[placeholder='Enter Full Name']");
    const emailInput = document.querySelector("input[placeholder='Enter Email']");
    const phoneInput = document.querySelector("input[placeholder='Enter Phone']");
    const addressInput = document.querySelector("input[placeholder='Enter Address']");
    const roleSelect = document.getElementById('roleSelect');

    if (!usernameInput || !passwordInput || !fullNameInput || !emailInput ||
        !phoneInput || !addressInput || !roleSelect) {
        console.error("One or more input elements are missing");
        showError("One or more input elements are missing");
        return;
    }

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    const fullName = fullNameInput.value.trim();
    const email = emailInput.value.trim();
    const address = addressInput.value.trim();
    const phone = phoneInput.value.trim();
    const role = roleSelect.value;

    if (!username || !password || !fullName || !email || !phone || !address || !role) {
        showError("Please fill in all fields");
        return;
    }

    const employeeData = {
        username: username,
        password: password,
        fullName: fullName,
        email: email,
        phone: phone,
        address: address,
        role: role
    };


    fetch("/admin/createEmployee", {
        method: "POST",
        headers:{
            'Authorization': "Bearer " + localStorage.getItem('jwt') || localStorage.getItem('access_token'),
            "Content-Type": "application/json"
        },
        body: JSON.stringify(employeeData)
    })
    .then(response =>{
        console.log(response);
        if (!response.ok)
            throw new Error('Failed to create employee');
        return response.json();
    })
    .then(data => {
        Swal.fire({
            icon: 'success',
            title: 'Success',
            text: 'New employee created successfully!'
        }).then(() =>{
            window.location.href = "/admin-employees";
        })
    })
        .catch(error  => {
            console.error('Error creating employee:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to create employee'
            });
        });

}