function saveUrl(){

    const token = localStorage.getItem(('jwt')) || localStorage.getItem(('access_token'));

    if (!token){
        const currentUrl = window.location.href;
        localStorage.setItem('returnUrl', currentUrl);

        window.location.href = "/login?returnUrl=" + currentUrl;
        return false;
    }
    return true;
}