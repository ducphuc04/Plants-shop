document.addEventListener("DOMContentLoaded", function () {
    // checkLoginStatus();

    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    const decodedToken = parseJwt(token);
    const username = decodedToken.sub;

    loadAccountDetail();

    document.getElementById('updateProfileBtn').addEventListener('click', updateProfile);
    document.getElementById('updatePasswordBtn').addEventListener('click', updatePassword);
});

function loadAccountDetail(){
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    fetch('/user/get-user-inf',{
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok){
                throw new Error('Failed to fetch user info');
            }
            return response.json();
        })
        .then(data => {
            console.log(data + "******");
            if (data && data.result){
                console.log(data.result);
                const user = data.result;
                document.getElementById('username').value = user.username || " ";
                document.getElementById('name').value = user.name || " ";
                document.getElementById('email').value = user.email || " ";
                document.getElementById('phone').value = user.phone || " ";
                document.getElementById('address').value = user.address || " ";
            }
        })
        .catch(error => {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: 'Không thể tải thông tin người dùng. Vui lòng thử lại sau.',
            });
        });
}

function parseJwt(token) {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
        return null;
    }
}

function updateProfile(){
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    const decodedToken = parseJwt(token);
    const username = decodedToken.sub;

    console.log(username);
    const profileData ={
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        address: document.getElementById('address').value
    }

    fetch(`/user/update-user/${username}`,{
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(profileData)
    })
        .then(response =>{
            console.log("111111");
            if (!response.ok)
                throw new Error('Failed to update profile');
            return response.json();
        })
        .then(data => {
            console.log(data);
            if (data && data.result) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công',
                    text: 'Thông tin tài khoản đã được cập nhật'
                });
            }
        })
        .catch(error => {
            console.error('Error updating profile:', error);
            Swal.fire({
            icon: 'error',
            title: 'Lỗi',
            text: 'Không thể cập nhật thông tin. Vui lòng thử lại sau.'
        });
    });
}

function updatePassword(){
    const token = localStorage.getItem('jwt') || localStorage.getItem('access_token');

    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    const decodedToken = parseJwt(token);
    const username = decodedToken.sub;

    const oldPassword = document.getElementById(('oldPassword')).value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (!oldPassword || !newPassword || !confirmPassword) {
        Swal.fire({
            icon: 'warning',
            title: 'Cảnh báo',
            text: 'Vui lòng điền đầy đủ thông tin!',
        });
        return;
    }

    if (newPassword !== confirmPassword) {
        Swal.fire({
            icon: 'warning',
            title: 'Cảnh báo',
            text: 'Mật khẩu mới và xác nhận mật khẩu không khớp!',
        });
        return;
    }

    const passwordData = {
        oldPassword: oldPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    fetch(`/user/update-user/${username}`, {
        method:'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(passwordData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update password');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.result) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công',
                    text: 'Cập nhật mật khẩu thành công!',
                });

                document.getElementById('oldPassword').value = "";
                document.getElementById('newPassword').value = "";
                document.getElementById('confirmPassword').value = "";
            }
        })
        .catch(error => {
            Swal.fire({
                icon: 'error',
                title: 'Lỗi',
                text: 'Cập nhật mật khẩu thất bại. Vui lòng thử lại sau.',
            });
        })
}
