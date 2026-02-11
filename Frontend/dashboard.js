const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html'; // kick out user if not logged in
}